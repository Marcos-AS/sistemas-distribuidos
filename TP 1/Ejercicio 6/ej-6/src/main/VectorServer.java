import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class VectorServer {

    private static final int PORT = 8000;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/vector", new VectorHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at port " + PORT);
    }

    private static class VectorHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!method.equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            if (query == null || query.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            String[] params = query.split("&");
            if (params.length != 2) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            double[] v1 = parseVector(params[0]);
            double[] v2 = parseVector(params[1]);
            if (v1 == null || v2 == null || v1.length != v2.length) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            double[] sum = new double[v1.length];
            double[] diff = new double[v1.length];
            for (int i = 0; i < v1.length; i++) {
                sum[i] = v1[i] + v2[i];
                diff[i] = v1[i] - v2[i];
            }
            String response = "Vector sum: " + Arrays.toString(sum) + "\nVector difference: " + Arrays.toString(diff);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private double[] parseVector(String param) {
            String[] tokens = param.split("=");
            if (tokens.length != 2 || !tokens[0].equalsIgnoreCase("v")) {
                return null;
            }
            String[] values = tokens[1].split(",");
            double[] vector = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                try {
                    vector[i] = Double.parseDouble(values[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return vector;
        }
    }
}
