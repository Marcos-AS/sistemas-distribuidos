package com.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
    String inputImagePath = "demo\\pexels-pixabay-461960.jpg";
    String outputImagePath = "demo\\sobel_result.jpg";

    try {
        // Cargar la imagen original
        BufferedImage image = ImageIO.read(new File(inputImagePath));

        // Convertir la imagen a escala de grises
        BufferedImage grayImage = convertToGrayscale(image);

        // Aplicar el operador de Sobel
        BufferedImage sobelImage = applySobelOperator(grayImage);

        // Guardar la nueva imagen resultante
        ImageIO.write(sobelImage, "jpg", new File(outputImagePath));

        System.out.println("Operador de Sobel aplicado con éxito. La imagen resultante se ha guardado en: " + outputImagePath);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }


    private static BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
    
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
    
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
    
                int grayValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);
    
                grayscaleImage.setRGB(x, y, grayValue << 16 | grayValue << 8 | grayValue);
            }
        }
    
        return grayscaleImage;
    }
    

private static BufferedImage applySobelOperator(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage sobelImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int[][] sobelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int pixelX = applyMask(image, x, y, sobelX);
            int pixelY = applyMask(image, x, y, sobelY);

            int gradient = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY);

            sobelImage.setRGB(x, y, gradient << 16 | gradient << 8 | gradient);
        }
    }

    return sobelImage;
    }

    private static int applyMask(BufferedImage image, int x, int y, int[][] mask) {
        int result = 0;
        int maskSize = mask.length;

        for (int i = 0; i < maskSize; i++) {
            for (int j = 0; j < maskSize; j++) {
                int pixel = image.getRGB(x - 1 + j, y - 1 + i);
                int grayValue = (pixel >> 16) & 0xFF;
                result += grayValue * mask[i][j];
            }
        }

        return result;
    }
}


