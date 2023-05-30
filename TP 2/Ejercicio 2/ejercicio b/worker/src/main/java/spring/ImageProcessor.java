package spring;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessor {
  @Resource
  private MessageConverter messageConverter;
  
  private final RabbitTemplate rabbitTemplate;
  
  @Autowired
  public ImageProcessor(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }
  
  @RabbitListener(queues = {"image-queue"})
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

      ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
      BufferedImage image = ImageIO.read(bais);
      BufferedImage grayImage = convertToGrayscale(image);
      BufferedImage sobelImage = applySobelOperator(grayImage);

      String outputDirectory = "C:\\Users\\leo_2\\OneDrive\\Documentos\\GitHub\\sistemas-distribuidos\\TP 2\\Ejercicio 2\\ejercicio b\\worker\\";
      String baseImageName = "image";
      String uniqueId = UUID.randomUUID().toString();
      String imagen = baseImageName + "_" + uniqueId  + ".jpg";
      String outputImagePath = outputDirectory + File.separator + imagen;

      //File imagenFile = new File(outputImagePath);
      //ImageIO.write(sobelImage, "jpg", imagenFile);
      //System.out.println("Imagen guardada correctamente en: " + imagenFile.getAbsolutePath());
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(sobelImage, "jpg", baos);
      byte[] imageData = baos.toByteArray();
      String id = UUID.randomUUID().toString();
      JSONObject jsonResult = new JSONObject();
      jsonResult.put("messageId", "2");
      jsonResult.put("pieces", numPieces);
      jsonResult.put("imageName", imageName);
      byte[] jsonBytesResult = jsonResult.toString().getBytes(StandardCharsets.UTF_8);
      int jsonLengthResult = jsonBytesResult.length;
      ByteBuffer bufferResult = ByteBuffer.allocate(4 + jsonLengthResult + imageData.length);
      bufferResult.putInt(jsonLengthResult);
      bufferResult.put(jsonBytesResult);
      bufferResult.put(imageData);
      byte[] combinedMessageResult = bufferResult.array();
      System.out.println(combinedMessageResult.length);
      this.rabbitTemplate.convertAndSend("result-queue", combinedMessageResult);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  private static BufferedImage convertToGrayscale(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage grayscaleImage = new BufferedImage(width, height, 10);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = image.getRGB(x, y);
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        int grayValue = (int)(0.299D * r + 0.587D * g + 0.114D * b);
        grayscaleImage.setRGB(x, y, grayValue << 16 | grayValue << 8 | grayValue);
      } 
    } 
    return grayscaleImage;
  }
  
  private static BufferedImage applySobelOperator(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage sobelImage = new BufferedImage(width, height, 10);
    int[][] sobelX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
    int[][] sobelY = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
    for (int y = 1; y < height - 1; y++) {
      for (int x = 1; x < width - 1; x++) {
        int pixelX = applyMask(image, x, y, sobelX);
        int pixelY = applyMask(image, x, y, sobelY);
        int gradient = (int)Math.sqrt((pixelX * pixelX + pixelY * pixelY));
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
        int grayValue = pixel >> 16 & 0xFF;
        result += grayValue * mask[i][j];
      } 
    } 
    return result;
  }
}