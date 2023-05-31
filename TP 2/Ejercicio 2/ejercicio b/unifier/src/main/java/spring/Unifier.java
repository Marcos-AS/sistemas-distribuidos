package spring;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;


import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class Unifier {
  @Resource
  private MessageConverter messageConverter;
  
  private Map<String, List<byte[]>> dividedImages = new HashMap<>();

  @RabbitListener(queues = {"result-queue"})
  public void processCombinedMessage(Message rabbitMessage) {
    try {
      byte[] combinedMessage = (byte[])this.messageConverter.fromMessage(rabbitMessage);
      ByteBuffer buffer = ByteBuffer.wrap(combinedMessage);
      int jsonLength = buffer.getInt();
      System.out.println("jsonLength: " + jsonLength);

      byte[] jsonBytes = new byte[jsonLength];
      byte[] imageBytes = new byte[combinedMessage.length - 4 - jsonLength];
      buffer.get(jsonBytes);
      buffer.get(imageBytes);

      String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
      JSONObject json = new JSONObject(jsonString);
      String messageId = json.getString("messageId");
      int numPieces = json.getInt("pieces");
      String imageName = json.getString("imageName");

      System.out.println("Received message: " + json);
      System.out.println("Message ID: " + messageId);
      System.out.println("Number of pieces: " + numPieces);
      System.out.println("Image name: " + imageName);

      /*ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
      BufferedImage image = ImageIO.read(bais);

      String outputDirectory = "C:\\Users\\leo_2\\OneDrive\\Documentos\\GitHub\\sistemas-distribuidos\\TP 2\\Ejercicio 2\\ejercicio b\\worker\\";
      String baseImageName = "image";
      String uniqueId = UUID.randomUUID().toString();
      String imagen = baseImageName + "_" + uniqueId  + ".jpg";
      String outputImagePath = outputDirectory + File.separator + imagen;

      File imagenFile = new File(outputImagePath);
      ImageIO.write(image, "jpg", imagenFile);
      System.out.println("Imagen guardada correctamente en: " + imagenFile.getAbsolutePath()); */
      
      // Almacenar el pedazo de imagen recibido
      if (!dividedImages.containsKey(messageId)) {
        dividedImages.put(messageId, new ArrayList<>());
      }
      dividedImages.get(messageId).add(imageBytes);

      // Verificar si se han recibido todos los pedazos
      if (dividedImages.get(messageId).size() == numPieces) {
        // Unificar la imagen
        BufferedImage unifiedImage = unifyImage(dividedImages.get(messageId), numPieces);

        // Guardar la imagen unificada
        saveUnifiedImage(unifiedImage, imageName);

        // Limpiar los datos de la imagen dividida
        dividedImages.remove(messageId);
      }

    } catch (Exception e) {
        e.printStackTrace();
    } 
  }

  private BufferedImage unifyImage(List<byte[]> imagePieces, int numPieces) {
    // Obtener información de los pedazos de imagen
    byte[] firstPiece = imagePieces.get(0);
    ByteArrayInputStream bais = new ByteArrayInputStream(firstPiece);
    try {
        BufferedImage firstImage = ImageIO.read(bais);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();

        // Crear un lienzo unificado
        BufferedImage unifiedImage = new BufferedImage(width * numPieces, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = unifiedImage.createGraphics();

        // Unir los pedazos de imagen en el lienzo unificado
        int x = 0;
        for (byte[] piece : imagePieces) {
            ByteArrayInputStream pieceBais = new ByteArrayInputStream(piece);
            BufferedImage pieceImage = ImageIO.read(pieceBais);
            g.drawImage(pieceImage, x, 0, null);
            x += width;
        }

        g.dispose();
        return unifiedImage;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
  }

    private void saveUnifiedImage(BufferedImage image, String imageName) {
      try {
        String outputDirectory = "C:\\Users\\leo_2\\OneDrive\\Documentos\\GitHub\\sistemas-distribuidos\\TP 2\\Ejercicio 2\\ejercicio b\\worker\\";
        String baseImageName = "image";
        String uniqueId = UUID.randomUUID().toString();
        String imagen = baseImageName + "_" + uniqueId  + ".jpg";
        String outputImagePath = outputDirectory + File.separator + imagen;
  
        File imagenFile = new File(outputImagePath);
        ImageIO.write(image, "jpg", imagenFile);
        System.out.println("Imagen guardada correctamente en: " + imagenFile.getAbsolutePath());
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
  }
