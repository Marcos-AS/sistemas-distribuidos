import java.io.*;
import java.net.*;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String serverMessage = in.readLine();
        System.out.println(serverMessage);
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String userInput = consoleIn.readLine();
            out.println(userInput);

            if (userInput.equals("get")) {
                serverMessage = in.readLine();
                System.out.println(serverMessage);
            }
        }
    }
}

