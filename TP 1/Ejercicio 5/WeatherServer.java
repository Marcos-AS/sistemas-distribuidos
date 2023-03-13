import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WeatherServer {

    private static final int PORT = 8000;
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new WeatherHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server started at port " + PORT);
    }

    private static class WeatherHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "No weather data available";
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("q=")) {
                String location = query.substring(2);
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY;
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                try {
                    HttpResponse<String> apiResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (apiResponse.statusCode() == 200) {
                        String apiData = apiResponse.body();
                        response = parseWeatherData(apiData);
                    }
                } catch (Exception e) {
                    System.out.println("Error calling OpenWeatherMap API: " + e.getMessage());
                }
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String parseWeatherData(String data) {
            // Parse JSON data from OpenWeatherMap API and extract relevant weather information
            // ...
            return "Current weather: sunny";
        }
    }
}
