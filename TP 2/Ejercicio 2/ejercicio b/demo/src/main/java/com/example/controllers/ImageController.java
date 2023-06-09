package com.example.controllers;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ImagePiece;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;



@RestController
public class ImageController {

    private final RabbitTemplate rabbitTemplate;
    private final String BUCKET_NAME = "bucket-imagenes-ej2b";

    @Value("${projectid}")
    private String projectId;

    @Autowired
    public ImageController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Storage inicializarCloud() throws FileNotFoundException, IOException {

        // Ruta del archivo JSON de las credenciales
        String rutaCredenciales = "C:\\Users\\marco\\OneDrive\\Documentos\\unlu-sdpp-tps-remote\\sistemas-distribuidos\\TP 2\\Ejercicio 2\\ejercicio b\\cloud\\terraform\\terraform.json";
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

    @PostMapping("/divide-image")
    public void divideImage(@RequestParam("file") MultipartFile file, @RequestParam("numPieces") int numPieces) throws FileNotFoundException, IOException {
        //List<ImagePiece> imagePieces = new ArrayList<>();

        String bucketName = BUCKET_NAME;
        Storage storage = inicializarCloud();

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            int pieceWidth = width / numPieces;
            int remainingWidth = width % numPieces;

            
            int x = 0;
            String idTarea = UUID.randomUUID().toString();
            for (int i = 0; i < numPieces; i++) {
                String id = UUID.randomUUID().toString();

                if (i == numPieces - 1) {
                    pieceWidth += remainingWidth;
                }

                //crea pedazo de la imagen
                BufferedImage piece = image.getSubimage(x, 0, pieceWidth, height);

                // Convertir la imagen a un array de bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(piece, "jpg", baos);
                byte[] imageData = baos.toByteArray();

                // Crear el objeto JSON
                JSONObject json = new JSONObject();
                json.put("messageId", idTarea);
                json.put("pieces", numPieces);
                json.put("imageName", id);

                // Convertir el objeto JSON a bytes
                byte[] jsonBytes = json.toString().getBytes(StandardCharsets.UTF_8);
                // int jsonLength = jsonBytes.length;

                // ByteBuffer buffer = ByteBuffer.allocate(4 + jsonLength);
                // buffer.putInt(jsonLength);
                // buffer.put(jsonBytes);
                // buffer.put(imageData);

                // byte[] combinedMessage = buffer.array();
                // System.out.println(combinedMessage.length);

                // Sube un archivo al bucket
                String nombreArchivoRemoto = id;
                System.out.println(id);
                BlobId blobId = BlobId.of(bucketName, nombreArchivoRemoto);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, imageData);

                // Enviar el mensaje combinado a la cola de RabbitMQ
                rabbitTemplate.convertAndSend("image-queue", jsonBytes);

                x += pieceWidth;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return imagePieces.size();
    }

    
}
