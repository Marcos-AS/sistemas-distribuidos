import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
public static void main(String[] args) throws IOException {

    // Dirección y puerto del servidor al que nos conectamos
    String servidor = "localhost";
    int puerto = 5000;

    // Creamos un socket para conectarnos al servidor
    Socket socket = new Socket(servidor, puerto);

    // Obtenemos los streams de entrada y salida para comunicarnos con el servidor
    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

    // Pedimos al usuario que ingrese su nombre de usuario
    System.out.println(input.readLine());
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    String nombreUsuario = teclado.readLine();
    output.println(nombreUsuario);

    // Ciclo principal del cliente
    while (true) {
        // Mostramos las opciones disponibles y pedimos al usuario que seleccione una
        System.out.println(input.readLine());
        String opcion = teclado.readLine();
        output.println(opcion);

        switch (opcion) {
            case "1":
                // Pedimos al usuario que ingrese el nombre del destinatario y el mensaje
                System.out.println(input.readLine());
                String destinatario = teclado.readLine();
                output.println(destinatario);
                System.out.println(input.readLine());
                String mensaje = teclado.readLine();
                output.println(mensaje);

                // Mostramos la respuesta del servidor
                System.out.println(input.readLine());
                break;

            case "2":
                // Mostramos los mensajes del usuario y esperamos a que presione Enter
                System.out.println(input.readLine());
                break;

            case "3":
                // Cerramos la conexión con el servidor y salimos del programa
                socket.close();
                System.exit(0);
                break;
        }
    }
}
}
