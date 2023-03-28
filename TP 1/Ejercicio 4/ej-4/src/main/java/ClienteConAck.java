import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteConAck {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        System.out.println("Conectado al servidor " + SERVER_HOST + ":" + SERVER_PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

       
            String message = "Hola servidor!";
            out.println(message);
            String msjRecibido = in.readLine();
            System.out.println(msjRecibido);
            if (msjRecibido != null)
                out.println("ACK");
            socket.close();
    }
}
