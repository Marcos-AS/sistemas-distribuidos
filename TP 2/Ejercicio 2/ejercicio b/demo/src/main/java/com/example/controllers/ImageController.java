package com.example.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ImagePiece;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

@RestController
public class ImageController {

    @PostMapping("/divide-image")
    public int divideImage(@RequestParam("file") MultipartFile file, @RequestParam("numPieces") int numPieces) {
        List<ImagePiece> imagePieces = new ArrayList<>();

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            int pieceWidth = width / numPieces;
            int remainingWidth = width % numPieces;

            int x = 0;
            for (int i = 0; i < numPieces; i++) {
                int pieceHeight = height;
                if (i == numPieces - 1) {
                    pieceWidth += remainingWidth;
                }

                BufferedImage piece = image.getSubimage(x, 0, pieceWidth, pieceHeight);
                ImagePiece imagePiece = new ImagePiece(i, piece);
                imagePieces.add(imagePiece);

                x += pieceWidth;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePieces.size();
    }
}
