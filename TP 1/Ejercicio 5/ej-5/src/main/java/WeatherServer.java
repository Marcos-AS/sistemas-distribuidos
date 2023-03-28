import java.io.IOException;
import java.util.Random;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WeatherServer {
    private static final int PORT = 8000;

    public static void main(String[] args) throws Exception {
        // Crea servidor HTTP
        HttpServer server = HttpServer.create();
        server.bind(new java.net.InetSocketAddress(PORT), 0);

        server.createContext("/", new ClimaHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Servidor escuchando en el puerto " + PORT);
    }

    static class ClimaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Genera datos del clima aleatorio y arma respuesta HTTP
            String response = generarClima();
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }

        private String generarClima() {
            Random random = new Random();
            int temperatura = random.nextInt(50);
            int humedad = random.nextInt(101);
            int viento = random.nextInt(101);
            String clima = "Temperatura: " + temperatura + " grados Celsius\n" +
                           "Humedad: " + humedad + "%\n" +
                           "Viento: " + viento + " km/h\n";
            return clima;
        }
    }
}