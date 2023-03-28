
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorConACK {
    private static final int PUERTO = 5000;
    private static final int MAX_CLIENTES = 10;
    private static final int MAX_MENSAJES = 100;

    public static void main(String[] args) throws IOException {
        // Crea una cola para almacenar los mensajes
        BlockingQueue<String> colaMensajes = new ArrayBlockingQueue<>(MAX_MENSAJES);

        try (ServerSocket servidorSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de colas iniciado en el puerto " + PUERTO);

            // Inicia un hilo para procesar cada cliente
            ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTES);
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                executor.execute(() -> procesarCliente(clienteSocket, colaMensajes));
            }
        }
    }

    private static void procesarCliente(Socket clienteSocket, BlockingQueue<String> colaMensajes) {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
             PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true)) {

            // Espera por un mensaje del cliente
            String mensaje = entrada.readLine();
            System.out.println("Recibido mensaje: " + mensaje);

            // Agrega el mensaje a la cola
            colaMensajes.put(mensaje);
            
            salida.println(mensaje);

            // Espera por un ACK del cliente
            String ack = entrada.readLine();
            if ("ACK".equals(ack)) {
                // Si el ACK es recibido, remueve el mensaje de la cola
                colaMensajes.remove(mensaje);
                System.out.println("Mensaje removido de la cola: " + mensaje);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}