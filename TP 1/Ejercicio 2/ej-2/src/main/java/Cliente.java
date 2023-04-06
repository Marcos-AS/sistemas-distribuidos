import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        // Configura la dirección y el puerto del servidor
        String serverAddress = "localhost";
        int serverPort = 8888;

        // Crea un socket de cliente TCP/IP y se conecta al servidor
        Socket socket = new Socket(serverAddress, serverPort);

        // Obtiene los flujos de entrada y salida del socket
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            // Enviar mensaje al servidor
            output.println(userInput);

            // Imprime la respuesta del servidor
            System.out.println("Respuesta del servidor: " + input.readLine());
        }

        // Cierra el socket
        socket.close();
    }
}
