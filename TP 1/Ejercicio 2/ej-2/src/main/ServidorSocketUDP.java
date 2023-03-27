import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServidorSocketUDP {
    private DatagramSocket serverSocket;

    public ServidorSocketUDP(int port) throws IOException {
        // Crea un socket de servidor UDP y lo asocia al puerto especificado
        serverSocket = new DatagramSocket(port);
        System.out.println("Servidor en ejecución en " + serverSocket.getLocalSocketAddress());
    }

    public void start() throws IOException {
        while (true) {
            // Crea un objeto DatagramPacket para recibir el mensaje del cliente
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Recibe el mensaje del cliente
            serverSocket.receive(packet);

            System.out.println("Cliente conectado desde " + packet.getAddress() + ":" + packet.getPort());

            // Repite el mensaje al cliente
            serverSocket.send(packet);
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorSocketUDP server = new MultiClientServerUDP(8888);
        server.start();
    }
}
