package com.example.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.controllers.ImageController;
import com.example.models.Task;
import com.example.repositories.TaskRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Blob.BlobSourceOption;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class TaskService {
    
    @Value("${projectid}")
    private String projectId;

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String BUCKET_NAME = "bucket-imagenes-ej2b";

    @Autowired
    private TaskRepository taskRepository;

    public Storage inicializarCloud() throws FileNotFoundException, IOException {

        // Ruta del archivo JSON de las credenciales
        String rutaCredenciales = "/app/terraform.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(rutaCredenciales));
    
        // Crea una instancia de StorageOptions con las credenciales y el ID del proyecto
        StorageOptions storageOptions = StorageOptions.newBuilder()
        .setCredentials(credentials)
        .setProjectId(projectId)
        .build();
    
        // Obtiene una instancia de Storage desde StorageOptions
        Storage storage = storageOptions.getService();

        return storage;
    
    }


    public ResponseEntity<String> divideImage(MultipartFile file, int numPieces) throws IOException {

        String bucketName = BUCKET_NAME;
        Storage storage = inicializarCloud();

        BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            int pieceWidth = width / numPieces;
            int remainingWidth = width % numPieces;

            int x = 0;
            String idTarea = UUID.randomUUID().toString();

            Task tarea = new Task();
            tarea.setId(idTarea);
            tarea.setEstado("PENDIENTE");
            tarea.setCantPartes(numPieces);
            tarea.setTiempo_inicio(LocalTime.now());
            tarea.setTiempo_fin(LocalTime.of(0, 0, 0));

            taskRepository.save(tarea);
                
            logger.info(String.format("Tarea insertada. [%s]", tarea));

            for (int i = 0; i < numPieces; i++) {

                String id = UUID.randomUUID().toString();

                if (i == numPieces - 1) {
                    pieceWidth += remainingWidth;
                }

                // Crea pedazos de la imagen
                BufferedImage piece = image.getSubimage(x, 0, pieceWidth, height);

                // Convierte la imagen a un array de bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(piece, "jpg", baos);
                byte[] imageData = baos.toByteArray();

                // Crear el objeto JSON
                JSONObject json = new JSONObject();
                json.put("messageId", idTarea);
                json.put("pieces", numPieces);
                json.put("imageName", id);
                json.put("originalName",file.getOriginalFilename());
                json.put("pieceNumber", i+1);

                // Convertir el objeto JSON a bytes
                byte[] jsonBytes = json.toString().getBytes(StandardCharsets.UTF_8);

                // Sube la imagen al bucket
                String nombreArchivoRemoto = id; // Pone como blobId al id que es nombre de la imagen
                System.out.println(id);
                BlobId blobId = BlobId.of(bucketName, nombreArchivoRemoto);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, imageData);

                // Enviar el mensaje combinado a la cola de RabbitMQ
                rabbitTemplate.convertAndSend("image-queue", jsonBytes);

                x += pieceWidth;
            }

            return ResponseEntity.ok("Imagen cargada con éxito :). ID de tarea: " + idTarea);

    }

    public ResponseEntity<?> unifiedImage(String idTarea) throws FileNotFoundException, IOException {

        // Buscamos en la base de datos la tarea solicitada
        Optional<Task> task = taskRepository.findById(idTarea);

        if (task.isPresent() && task.get().getEstado().equals("PENDIENTE")) {
            logger.info(String.format("Tarea en estado pendiente..."));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("La imagen se encuentra en procesamiento.");
        }

        if (task.isPresent() && task.get().getEstado().equals("TERMINADO")) {
            
            Storage storage = inicializarCloud();
            BlobId blobId = BlobId.of(BUCKET_NAME, task.get().getId());
            Blob blob = storage.get(blobId);

            // Verificar si la imagen existe en el bucket
            if (blob != null && blob.exists()) {
                ByteArrayInputStream bais = new ByteArrayInputStream(blob.getContent(BlobSourceOption.generationMatch()));
                BufferedImage image = ImageIO.read(bais);

                // Guardar la imagen en un archivo temporal
                File tempFile = File.createTempFile(task.get().getId(), ".jpg");
                ImageIO.write(image, "jpeg", tempFile);

                // Devolver la imagen al usuario
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(task.get().getId()).build());
                
                logger.info(String.format("Tarea terminada. Entregando..."));

                // Eliminamos la imagen del bucket
                storage.delete(BUCKET_NAME, task.get().getId());
                return new ResponseEntity<>(new FileSystemResource(tempFile), headers, HttpStatus.OK);
            } else {
                logger.info(String.format("La imagen no existe en el bucket"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La imagen no se encontró en el bucket");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una imagen asociada a dicho ID.");

    }


    public ResponseEntity<String> getTaskTime(String idTarea) {
        
        // Buscamos en la base de datos la tarea solicitada
        Optional<Task> task = taskRepository.findById(idTarea);

        long segundos = task.get().getTiempo_fin().toSecondOfDay() - task.get().getTiempo_inicio().toSecondOfDay();
        LocalTime time = LocalTime.ofSecondOfDay(segundos);
        return ResponseEntity.status(HttpStatus.OK).body("Tiempo: " + time);

    }

}
