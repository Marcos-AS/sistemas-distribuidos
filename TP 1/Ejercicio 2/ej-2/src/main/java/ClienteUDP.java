import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {
    public static void main(String[] args) throws IOException {
        // Configura la dirección y el puerto del servidor
        String serverAddress = "localhost";
        int serverPort = 8888;

        // Crea un socket de cliente UDP
        DatagramSocket socket = new DatagramSocket();

        // Crea un objeto DatagramPacket para enviar el mensaje al servidor
        String message = "Hola, servidor";
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverAddress), serverPort);

        // Envía el mensaje al servidor
        socket.send(packet);

        // Crea un objeto DatagramPacket para recibir la respuesta del servidor
        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

        // Recibe la respuesta del servidor
        socket.receive(responsePacket);
        String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

        // Imprime la respuesta del servidor
        System.out.println(response);

        // Cierra el socket
        socket.close();
    }
}
