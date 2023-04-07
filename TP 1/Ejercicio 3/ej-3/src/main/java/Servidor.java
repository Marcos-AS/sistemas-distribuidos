import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Servidor {

    // Mapa que almacena las colas de mensajes de cada usuario
    private static Map<String, Queue<String>> colasMensajes = new HashMap<>();

    public static void main(String[] args) throws IOException {

        // Puerto en el que el servidor espera conexiones de clientes
        int puerto = 5000;

        // Creamos un ServerSocket que escucha en el puerto indicado
        ServerSocket serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en el puerto " + puerto);

        while (true) {
            // Esperamos a que llegue una conexión de un cliente
            Socket socket = serverSocket.accept();

            // Creamos un nuevo hilo para manejar la conexión con el cliente
            Thread thread = new Thread(() -> {
                try {
                    System.out.println("Cliente conectado desde la dirección " + socket.getInetAddress());

                    // Obtenemos los streams de entrada y salida para comunicarnos con el cliente
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                    // Pedimos al cliente que ingrese su nombre de usuario
                    output.println("Bienvenido al servidor de mensajes. Por favor, ingrese su nombre de usuario:");
                    String nombreUsuario = input.readLine();

                    // Verificamos si el usuario ya tiene una cola de mensajes
                    if (!colasMensajes.containsKey(nombreUsuario)) {
                        colasMensajes.put(nombreUsuario, new LinkedList<>());
                    }

                    // Ciclo principal del cliente
                    while (true) {
                        // Pedimos al cliente que seleccione una opción
                        output.println("Seleccione una opción: 1. Dejar mensaje 2. Ver mensajes 3. Salir");
                        String opcion = input.readLine();

                        switch (opcion) {
                            case "1":
                                // Pedimos al cliente que ingrese el nombre del destinatario y el mensaje
                                output.println("Ingrese el nombre del destinatario:");
                                String destinatario = input.readLine();
                                output.println("Ingrese el mensaje:");
                                String mensaje = input.readLine();

                                // Verificamos si el destinatario existe y dejamos el mensaje en su cola
                                if (colasMensajes.containsKey(destinatario)) {
                                    colasMensajes.get(destinatario).offer(nombreUsuario + ": " + mensaje);
                                    output.println("Mensaje enviado correctamente.");
                                } else {
                                    output.println("El usuario " + destinatario + " no existe.");
                                }
                                break;

                            case "2":
                                // Mostramos los mensajes en la cola del usuario y los eliminamos
                                Queue<String> colaMensajes = colasMensajes.get(nombreUsuario);
                                if (!colaMensajes.isEmpty()) {
                                    output.println("Mensajes para " + nombreUsuario + ":");
                                    while (!colaMensajes.isEmpty()) {
                                        output.println(colaMensajes.poll());
                                    }
                                } else {
                                    output.println("No tiene mensajes nuevos.");
                                }
                                break;

                            case "3":
                                // Cerramos la conexión con el cliente
                                socket.close();
                                System.out.println("Cliente desconectado.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error al comunicarse con el cliente.");
                }
            });
            
            thread.start();
        }
    }
}
                    
                        
