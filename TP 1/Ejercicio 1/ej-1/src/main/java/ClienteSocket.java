import java.net.*;
import java.io.*;

public class ClienteSocket {
    public static void main(String[] args) throws IOException {
        // Establecer el host y el puerto del servidor
        String hostName = "localhost";
        int portNumber = 65432;

        try (
            // Crear un socket y conectar al servidor
            Socket socket = new Socket(hostName, portNumber);
            // Crear streams para enviar y recibir datos
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Leer entrada del usuario
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Enviar mensaje al servidor
                out.println(userInput);
                // Leer respuesta del servidor y mostrarla en pantalla
                System.out.println("Respuesta del servidor: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error en la conexión con el servidor: " + e.getMessage());
            System.exit(1);
        }
    }
}


