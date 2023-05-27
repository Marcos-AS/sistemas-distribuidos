package spring;

public class ImageMessage {
    private String messageId;
    private int pieces;
    private String imageName;
    private byte[] imageData;

    // Agrega los getters y setters

    @Override
    public String toString() {
        return "ImageMessage{" +
                "messageId='" + messageId + '\'' +
                ", pieces=" + pieces +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}
