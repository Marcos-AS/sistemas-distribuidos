package com.example.controllers;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ImagePiece;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

@RestController
public class ImageController {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ImageController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/divide-image")
    public int divideImage(@RequestParam("file") MultipartFile file, @RequestParam("numPieces") int numPieces) {
        List<ImagePiece> imagePieces = new ArrayList<>();

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            int pieceWidth = width / numPieces;
            int remainingWidth = width % numPieces;

            String id = UUID.randomUUID().toString();

            int x = 0;
            for (int i = 0; i < numPieces; i++) {
                int pieceHeight = height;
                if (i == numPieces - 1) {
                    pieceWidth += remainingWidth;
                }

                BufferedImage piece = image.getSubimage(x, 0, pieceWidth, pieceHeight);
                ImagePiece imagePiece = new ImagePiece(i, piece);
                imagePieces.add(imagePiece);

                // Convertir la imagen a un array de bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(piece, "jpg", baos);
                byte[] imageData = baos.toByteArray();

                // Crear el objeto JSON
                JSONObject json = new JSONObject();
                json.put("messageId", id);
                json.put("pieces", numPieces);
                json.put("imageName", file.getOriginalFilename());

                // Convertir el objeto JSON a bytes
                byte[] jsonBytes = json.toString().getBytes(StandardCharsets.UTF_8);

                // Crear un arreglo de bytes para el mensaje combinado
                byte[] combinedMessage = new byte[imageData.length + jsonBytes.length];
                System.arraycopy(jsonBytes, 0, combinedMessage, 0, jsonBytes.length);
                System.arraycopy(imageData, 0, combinedMessage, jsonBytes.length, imageData.length);

                // Enviar el mensaje combinado a la cola de RabbitMQ
                rabbitTemplate.convertAndSend("image-queue", combinedMessage);

                x += pieceWidth;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePieces.size();
    }
}
