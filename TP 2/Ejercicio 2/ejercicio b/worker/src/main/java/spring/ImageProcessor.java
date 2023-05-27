package spring;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessor {

    @RabbitListener(queues = "image-queue")
    public void processCombinedMessage(byte[] combinedMessage) {
        // Extraer los bytes del JSON y los bytes de la imagen
        int jsonLength = getJsonLength(combinedMessage); // Obtener la longitud del JSON
        byte[] jsonBytes = new byte[jsonLength];
        byte[] imageBytes = new byte[combinedMessage.length - jsonLength];
        System.arraycopy(combinedMessage, 0, jsonBytes, 0, jsonLength);
        System.arraycopy(combinedMessage, jsonLength, imageBytes, 0, imageBytes.length);

        // Deserializar el JSON
        String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(jsonString);

        // Obtener los valores del mensaje JSON
        String messageId = json.getString("messageId");
        int numPieces = json.getInt("pieces");
        String imageName = json.getString("imageName");

        // Realizar cualquier procesamiento adicional necesario con los valores obtenidos

        // Trabajar con los bytes de la imagen, si es necesario
        // BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        // ... realizar operaciones con la imagen ...

        System.out.println("Received message: " + json);
        System.out.println("Message ID: " + messageId);
        System.out.println("Number of pieces: " + numPieces);
        System.out.println("Image name: " + imageName);
    }

    private int getJsonLength(byte[] combinedMessage) {
        int jsonLength = 0;
        for (int i = 0; i < combinedMessage.length; i++) {
            if (combinedMessage[i] == '{') {
                jsonLength = i;
                break;
            }
        }
        return jsonLength;
    }

    

}