import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServidorSocketUDP {

    public static void main(String[] args) {
        int puerto = 12345;
        DatagramSocket socket = null;

        try {
            // Crea el socket en el puerto especificado
            socket = new DatagramSocket(puerto);
            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {
                // Crea un buffer para almacenar el mensaje recibido
                byte[] buffer = new byte[1024];
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);

                // Espera la llegada de un paquete
                socket.receive(paqueteRecibido);

                // Obtiene los datos del paquete recibido
                InetAddress direccionCliente = paqueteRecibido.getAddress();
                int puertoCliente = paqueteRecibido.getPort();
                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                System.out.println("Mensaje recibido del cliente " + direccionCliente.getHostAddress() + ": " + mensaje);

                // Envía una respuesta al cliente
                String respuesta = "Respuesta desde el servidor: " + mensaje.toUpperCase();
                byte[] datosRespuesta = respuesta.getBytes();
                DatagramPacket paqueteRespuesta = new DatagramPacket(datosRespuesta, datosRespuesta.length, direccionCliente, puertoCliente);
                socket.send(paqueteRespuesta);
            }

        } catch (SocketException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}