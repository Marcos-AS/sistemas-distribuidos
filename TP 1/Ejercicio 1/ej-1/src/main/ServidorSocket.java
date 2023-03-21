import java.net.*;
import java.io.*;

public class ServidorSocket {
    public static void main(String[] args) throws IOException {
        // Establecer el puerto del servidor
        int portNumber = 65432;
        
        try (
            // Crear un objeto ServerSocket y vincularlo al puerto
            ServerSocket serverSocket = new ServerSocket(portNumber);
            // Aceptar conexiones entrantes
            Socket clientSocket = serverSocket.accept();
            // Crear streams para recibir y enviar datos
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            System.out.println("Servidor esperando conexiones en el puerto " + portNumber);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido: " + inputLine);
                // Enviar la respuesta de vuelta al cliente
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.err.println("Error en la conexión con el cliente: " + e.getMessage());
        }
    }
}
