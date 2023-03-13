import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocketMulticliente {

    public static void main(String[] args) {
        int puerto = 12345;
        ServerSocket servidor = null;

        try {
            // Crea el servidor en el puerto especificado
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {
                // Espera la conexión del cliente
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde " + cliente.getInetAddress().getHostName());

                // Crea un hilo para manejar la conexión con el cliente
                Thread hilo = new Thread(new ManejadorCliente(cliente));
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        } finally {
            try {
                if (servidor != null) {
                    servidor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ManejadorCliente implements Runnable {

    private Socket cliente;

    public ManejadorCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public void run() {
        try {
            // Obtiene los flujos de entrada y salida del cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);

            // Lee los datos enviados por el cliente y los procesa
            String mensaje = entrada.readLine();
            System.out.println("Mensaje recibido del cliente " + cliente.getInetAddress().getHostName() + ": " + mensaje);
            String respuesta = "Respuesta desde el servidor: " + mensaje.toUpperCase();
            salida.println(respuesta);

            // Cierra la conexión con el cliente
            cliente.close();
            System.out.println("Cliente desconectado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
