import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ServidorVector {
    
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/vector", new VectorHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado en el puerto 8080.");

        // Ejemplos de consultas a realizar
        // http://localhost:8080/vector/suma?a=1,2,3&b=4,5,6
        // http://localhost:8080/vector/resta?a=1,2,3&b=4,5,6
    }

    /*static class VectorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getQuery();
            String[] aValues = parseVector(query, "a");
            String[] bValues = parseVector(query, "b");
            int[] a = new int[aValues.length];
            int[] b = new int[bValues.length];
            for (int i = 0; i < aValues.length; i++) {
                a[i] = Integer.parseInt(aValues[i]);
            }
            for (int i = 0; i < bValues.length; i++) {
                b[i] = Integer.parseInt(bValues[i]);
            }
            String op = t.getRequestURI().getPath().split("/")[2];
            int[] result;
            // Introducir error: multiplicar el primer elemento del vector a por 10
                a[0] *= 10;
            if (op.equals("suma")) {
                result = sumVectors(a, b);  
            } else if (op.equals("resta")) {
                result = subtractVectors(a, b);
            } else {
                String response = "Operación no soportada.";
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            String response = Arrays.toString(result);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        }
    } */

    static class VectorHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
    String query = t.getRequestURI().getQuery();
    String[] aValues = parseVector(query, "a");
    String[] bValues = parseVector(query, "b");
    int[] a = new int[aValues.length];
    int[] b = new int[bValues.length];
    for (int i = 0; i < aValues.length; i++) {
        a[i] = Integer.parseInt(aValues[i]);
    }
    for (int i = 0; i < bValues.length; i++) {
        b[i] = Integer.parseInt(bValues[i]);
    }
    String op = t.getRequestURI().getPath().split("/")[2];
    int[] result;

    // Convertir los vectores a una cadena para mostrarlos en el navegador
    String aStr = Arrays.toString(a);
    String bStr = Arrays.toString(b);

    // Construir la respuesta que se enviará al navegador
    String response = "<html><body>" +
                      "<h1>Valores de los vectores:</h1>" +
                      "<p>Vector A: " + aStr + "</p>" +
                      "<p>Vector B: " + bStr + "</p>";

    // Realizar la operación correspondiente
    // Introducir error: multiplicar el primer elemento del vector a por 10
    a[0] *= 10;
    if (op.equals("suma")) {
        result = sumVectors(a, b);
        response += "<h1>Resultado de la suma:</h1>";
    } else if (op.equals("resta")) {
        result = subtractVectors(a, b);
        response += "<h1>Resultado de la resta:</h1>";
    } else {
        response = "Operación no soportada.";
        t.sendResponseHeaders(400, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        return;
    }

    // Convertir el resultado a una cadena y agregarlo a la respuesta
    String resultStr = Arrays.toString(result);
    response += "<p>" + resultStr + "</p>" +
                "</body></html>";

    // Enviar la respuesta al navegador
    t.sendResponseHeaders(200, response.length());
    OutputStream os = t.getResponseBody();
    os.write(response.getBytes());
    os.close();
    }
}


    private static String[] parseVector(String query, String vectorName) {
        String[] values = query.split("&");
        for (String value : values) {
            String[] tokens = value.split("=");
            if (tokens[0].equals(vectorName)) {
                return tokens[1].split(",");
            }
        }
        return null;
    }

    private static int[] sumVectors(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma longitud.");
        }
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    private static int[] subtractVectors(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Los vectores deben tener la misma longitud.");
        }
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }
}
