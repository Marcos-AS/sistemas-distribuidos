import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorColas {
    private static final int PUERTO = 5000;
    private static final int NUM_HILOS = 10;
    private static final int CAPACIDAD_COLA = 100;
    private static BlockingQueue<String> colaMensajes = new LinkedBlockingQueue<>(CAPACIDAD_COLA); //cola de strings

    public static void main(String[] args) throws IOException {
        ServerSocket servidorSocket = new ServerSocket(PUERTO);
        ExecutorService executor = Executors.newFixedThreadPool(NUM_HILOS);

        System.out.println("Servidor escuchando en el puerto " + PUERTO);
        while (true) {
            Socket clienteSocket = servidorSocket.accept();
            System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress().getHostAddress() + ":" + clienteSocket.getPort());

            // Crea un hilo para manejar al cliente
            ManejadorCliente manejador = new ManejadorCliente(clienteSocket);
            executor.execute(manejador); //ejecuta el comando Runnable dado (manejador), en un hilo en algún momento en el futuro
        }
    }

    static class ManejadorCliente implements Runnable {
        private Socket clienteSocket;

        public ManejadorCliente(Socket clienteSocket) {
            this.clienteSocket = clienteSocket;
        }

        public void run() {
            try (BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                 PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true)) {

                while (true) {
                    // Lee el mensaje enviado por el cliente
                    String mensaje = entrada.readLine();
                    if (mensaje == null) {
                        // El cliente ha cerrado la conexión
                        System.out.println("Cliente desconectado");
                        break;
                    }

                    // Agrega el mensaje a la cola
                    colaMensajes.put(mensaje);

                    // Envía una respuesta al cliente
                    salida.println("Mensaje recibido: " + mensaje);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}