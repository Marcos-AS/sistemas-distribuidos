package com.example;

import java.awt.image.BufferedImage;

public class ImagePiece {
    private int id;
    private BufferedImage image;

    public ImagePiece(int id, BufferedImage image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public BufferedImage getImage() {
        return image;
    }
}
