import java.net.*;
import java.io.*;

public class ClienteSocket {
    public static void main(String[] args) {
        try {
            // Conectar al servidor
            Socket socket = new Socket("localhost", 12245);

            // Obtener los streams de entrada y salida
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
            InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            // Enviar un mensaje al servidor
            out.println("Hola, servidor!");

            // Leer la respuesta del servidor
            String respuesta = in.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

            // Cerrar la conexión
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
