package spring;

import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob.BlobSourceOption;

@Component
public class ImageProcessor {

  @Autowired
  private MessageConverter messageConverter;
  
  @Value("${projectid}")
  private String projectId;

  private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

  private final RabbitTemplate rabbitTemplate;
  
  @Autowired
  public ImageProcessor(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  private final String BUCKET_NAME = "bucket-imagenes-ej2b";
  
  @RabbitListener(queues = {"image-queue"})
  public void processMessage(Message rabbitMessage) {
    try {

      Storage storage = inicializarCloud();

      byte[] mensaje = (byte[])this.messageConverter.fromMessage(rabbitMessage);
      String jsonString = new String(mensaje, StandardCharsets.UTF_8);
      JSONObject json = new JSONObject(jsonString);
      String messageId = json.getString("messageId");
      int numPieces = json.getInt("pieces");
      String imageName = json.getString("imageName");
      int pieceNumber = json.getInt("pieceNumber");

      logger.info(String.format("Mensaje recibido."));
      logger.info(String.format("Message ID: " + messageId));
      logger.info(String.format("Number of pieces: " + numPieces));
      logger.info(String.format("Image name: " + imageName));
      logger.info(String.format("Piece number: " + pieceNumber));

      //obtiene la imagen desde el bucket
      BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
      Blob blob = storage.get(blobId);

      //convierte a bytes la imagen del bucket para aplicarle el filtro que espera bufferedImage
      ByteArrayInputStream bais = new ByteArrayInputStream(blob.getContent(BlobSourceOption.generationMatch()));
      BufferedImage image = ImageIO.read(bais);
      BufferedImage grayImage = convertToGrayscale(image);
      BufferedImage sobelImage = applySobelOperator(grayImage);

      storage.delete(BUCKET_NAME, imageName);

      //convierte a byte array y sube al bucket la imagen con filtro
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(sobelImage, "jpg", baos);
      byte[] imageData = baos.toByteArray();
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      storage.create(blobInfo, imageData);

      this.rabbitTemplate.convertAndSend("result-queue", rabbitMessage);

    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public Storage inicializarCloud() throws FileNotFoundException, IOException {

    // Ruta del archivo JSON de las credenciales
    String rutaCredenciales = "/app/terraform.json"; 
    //System.getenv("GOOGLE_CREDENTIALS");
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