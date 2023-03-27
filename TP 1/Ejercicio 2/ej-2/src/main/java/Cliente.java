import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        // Configura la dirección y el puerto del servidor
        String serverAddress = "localhost";
        int serverPort = 8888;

        // Crea un socket de cliente TCP/IP y se conecta al servidor
        Socket socket = new Socket(serverAddress, serverPort);

        // Obtiene los flujos de entrada y salida del socket
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();

        // Envía un mensaje de texto al servidor
        String message = "Hola, servidor";
        output.write(message.getBytes());

        // Lee la respuesta del servidor
        byte[] buffer = new byte[1024];
        int bytesRead = input.read(buffer);
        String response = new String(buffer, 0, bytesRead);

        // Imprime la respuesta del servidor
        System.out.println(response);

        // Cierra el socket
        socket.close();
    }
}
