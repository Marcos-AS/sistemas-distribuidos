package com.example.controllers;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Blob.BlobSourceOption;

import io.micrometer.common.util.StringUtils;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.io.File;

import javax.imageio.ImageIO;


import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;

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

    @PostMapping("/divide-image")
    public ResponseEntity<String> divideImage(@RequestParam("file") MultipartFile file, @RequestParam("numPieces") int numPieces) throws FileNotFoundException, IOException {

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

            return ResponseEntity.ok("Imagen cargada con éxito :)");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cargar la imagen :("); 
        }

    }

    @GetMapping("/unified-image")
    public ResponseEntity<?> unifiedImage(@RequestParam("nombreImagen") String imageName) throws IOException {
        
    /*    try {
            if (StringUtils.isNotBlank(imageName)) {
                Storage storage = inicializarCloud();
                BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
                Blob blob = storage.get(blobId);

                if (blob.exists()) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(blob.getContent(BlobSourceOption.generationMatch()));
                    BufferedImage image = ImageIO.read(bais);

                    response.setContentType("image/jpeg");
                    OutputStream out = response.getOutputStream();
                    ImageIO.write(image, "jpeg", out);
                    out.close();

                    storage.delete(BUCKET_NAME, imageName);

                    response.getWriter().write("Imagen descargada con éxito :)");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Imagen no encontrada en el servidor.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Parámetro 'nombreImagen' requerido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al procesar la imagen.");
        }
    } */

    try {
        Storage storage = inicializarCloud();
        BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
        Blob blob = storage.get(blobId);

        // Verificar si la imagen existe en el bucket
        if (blob != null && blob.exists()) {
            ByteArrayInputStream bais = new ByteArrayInputStream(blob.getContent(BlobSourceOption.generationMatch()));
            BufferedImage image = ImageIO.read(bais);

            // Guardar la imagen en un archivo temporal
            File tempFile = File.createTempFile(imageName, ".jpg");
            ImageIO.write(image, "jpeg", tempFile);

            // Devolver la imagen al usuario
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(imageName).build());
            
            // Eliminamos la imagen del bucket
            storage.delete(BUCKET_NAME, imageName);

            return new ResponseEntity<>(new FileSystemResource(tempFile), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La imagen no se encontró en el bucket");
        }
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la imagen");
    }
    }
}
