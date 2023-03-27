import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocketMulticliente {
    private ServerSocket serverSocket;

    public ServidorSocketMulticliente(int port) throws IOException {
        // Crea un socket de servidor TCP/IP y lo asocia al puerto especificado
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor en ejecución en " + serverSocket.getLocalSocketAddress());
    }

    public void start() throws IOException {
        while (true) {
            // Acepta una conexión entrante
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getRemoteSocketAddress());

            // Crea un nuevo hilo para manejar al cliente
            Thread clientThread = new Thread(new ClientHandler(clientSocket));
            clientThread.start();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // Obtiene los flujos de entrada y salida del socket del cliente
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                // Lee los datos del cliente
                byte[] buffer = new byte[1024];
                int bytesRead = input.read(buffer);
                String message = new String(buffer, 0, bytesRead);

                // Repite los datos al cliente
                output.write(buffer, 0, bytesRead);

                // Cierra el socket del cliente
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorSocketMulticliente server = new ServidorSocketMulticliente(8888);
        server.start();
    }
}
