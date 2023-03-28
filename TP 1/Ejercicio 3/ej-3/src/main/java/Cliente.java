import java.io.*;
import java.net.*;

public class Cliente {
    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket(SERVIDOR, PUERTO);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conexión establecida con el servidor");

            // Envía varios mensajes al servidor
            for (int i = 1; i <= 5; i++) {
                String mensaje = "Mensaje " + i;
                salida.println(mensaje);
                System.out.println("Enviado: " + mensaje);

                // Espera la respuesta del servidor
                String respuesta = entrada.readLine();
                System.out.println("Recibido: " + respuesta);
            }

            // Cierra la conexión con el servidor
            System.out.println("Cerrando conexión");
        }
    }
}