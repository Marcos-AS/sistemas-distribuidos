package spring;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import spring.services.TaskService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob.BlobSourceOption;

@Component
public class Unifier {

  @Autowired
  private TaskService taskService;

  @Autowired
  private MessageConverter messageConverter;
  
  @Value("${projectid}")
  private String projectId;

  private static final Logger logger = LoggerFactory.getLogger(Unifier.class);

  private final String BUCKET_NAME = "bucket-imagenes-ej2b";

  // Usar TreeMap para almacenar los pedazos de imagen
  private Map<String, TreeMap<Integer, byte[]>> dividedImages = new HashMap<>();

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

  @RabbitListener(queues = {"result-queue"})
  public void processMessage(Message rabbitMessage) {
    try {
      Storage storage = inicializarCloud();

      byte[] mensaje = (byte[])this.messageConverter.fromMessage(rabbitMessage);

      String jsonString = new String(mensaje, StandardCharsets.UTF_8);
      JSONObject json = new JSONObject(jsonString);
      String idTarea = json.getString("messageId");
      int numPieces = json.getInt("pieces");
      String imageName = json.getString("imageName");
      int pieceNumber = json.getInt("pieceNumber");

      logger.info(String.format("Mensaje recibido."));
      logger.info(String.format("Message ID: " + idTarea));
      logger.info(String.format("Number of pieces: " + numPieces));
      logger.info(String.format("Image name: " + imageName));
      logger.info(String.format("Piece number: " + pieceNumber));

      BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
      Blob blob = storage.get(blobId);

      ByteArrayInputStream bais = new ByteArrayInputStream(blob.getContent(BlobSourceOption.generationMatch()));
      BufferedImage image = ImageIO.read(bais);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", baos);
      byte[] imageData = baos.toByteArray();

      // Almacenar el pedazo de imagen recibido
      if (!dividedImages.containsKey(idTarea)) {
        dividedImages.put(idTarea, new TreeMap<>());
      }
      dividedImages.get(idTarea).put(pieceNumber, imageData);

      // Verificar si se han recibido todos los pedazos
      if (dividedImages.get(idTarea).size() == numPieces) {
        
        // Obtener los pedazos de imagen en orden
        List<byte[]> orderedPieces = new ArrayList<>(dividedImages.get(idTarea).values());

        // Unificar la imagen
        BufferedImage unifiedImage = unifyImage(orderedPieces, numPieces);

        // Guardar la imagen unificada
        saveUnifiedImage(unifiedImage, idTarea);

        // Cambiamos el estado de la tarea a "TERMINADO"
        taskService.updateState(idTarea);

        // Limpiar los datos de la imagen dividida
        dividedImages.remove(idTarea);
      }

      //borra cada pedazo del bucket
      storage.delete(BUCKET_NAME, imageName);

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

  // Guarda en el bucket con el nombre original de la imagen que envió el usuario
  private void saveUnifiedImage(BufferedImage image, String imageName) {
      try {

        logger.debug(
          String.format(
              "Se ejecuta el método saveUnifiedImage. [idTarea = %s]",
              imageName
          )
        );

        Storage storage = inicializarCloud();
        BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, imageData);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
  
  }
