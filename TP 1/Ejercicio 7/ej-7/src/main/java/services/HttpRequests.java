package services;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequests {

    
    public void HttpRequests(){
        
    }

    
    public HttpResponse<String>  PostHttpRequest(String url , HashMap<String, String> parametersMap) throws IOException, InterruptedException {

        System.out.println("HOLI"+url+"&"+parametersMap);

        HttpResponse<String> response = null;
        ObjectMapper objectMapper = new ObjectMapper();
        //arma el cuerpo de la petición, serializando los parámetros (a string)
        String requestBody = objectMapper.writeValueAsString(parametersMap);
        System.out.println("Request body "+requestBody);
        try {
                HttpClient client = HttpClient.newHttpClient();
                //arma la petición con método POST
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                
                //se envía la petición sincrónicamente
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
                System.out.println(response.body());
        } catch (Exception e) {
               System.err.println("ERROR: "+e.getMessage());
        }
       
        return response;
    }
}

