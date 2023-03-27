import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class VectorServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/sum", new SumHandler());
        server.createContext("/sub", new SubHandler());
        server.setExecutor(null);
        server.start();
    }

    static class SumHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String[] values = exchange.getRequestURI().getQuery().split("&");
                int[] vector1 = parseVector(values[0]);
                int[] vector2 = parseVector(values[1]);
                int[] result = new int[vector1.length];

                for (int i = 0; i < vector1.length; i++) {
                    result[i] = vector1[i] * vector2[i]; // Introducimos el error: multiplicamos en lugar de sumar
                }

                exchange.sendResponseHeaders(200, result.length);
                exchange.getResponseBody().write(toByteArray(result));
                exchange.close();
            }
        }
    }

    static class SubHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String[] values = exchange.getRequestURI().getQuery().split("&");
                int[] vector1 = parseVector(values[0]);
                int[] vector2 = parseVector(values[1]);
                int[] result = new int[vector1.length];

                for (int i = 0; i < vector1.length; i++) {
                    result[i] = vector1[i] * vector2[i]; // Introducimos el error: multiplicamos en lugar de restar
                }

                exchange.sendResponseHeaders(200, result.length);
                exchange.getResponseBody().write(toByteArray(result));
                exchange.close();
            }
        }
    }

    static int[] parseVector(String vector) {
        String[] values = vector.split(",");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    static byte[] toByteArray(int[] vector) {
        byte[] result = new byte[4 * vector.length];
        for (int i = 0; i < vector.length; i++) {
            int value = vector[i];
            result[i * 4] = (byte) (value >> 24);
            result[i * 4 + 1] = (byte) (value >> 16);
            result[i * 4 + 2] = (byte) (value >> 8);
            result[i * 4 + 3] = (byte) (value);
        }
        return result;
    }
}
