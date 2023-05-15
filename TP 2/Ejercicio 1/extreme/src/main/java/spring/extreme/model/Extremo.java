package spring.extreme.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class Extremo {

    //para enviar peticiones
    RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port}")
    private String numPuerto;

    // curl -X POST -H "Content-Type: application/json" -d '{"direccionIp":"192.168.1.1", "puerto": 8080}' http://<direccion_ip>:<puerto>

    public void informarMaestro(String direccionIp, int puerto) {
        
        try {
            // Set the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Serialize the request body to JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            // Crear objeto que contiene la lista de archivos y el número de puerto
            Map<String, Object> objetoJson = new HashMap<>();
            objetoJson.put("archivos", listaArchivosDisponibles());
            objetoJson.put("puerto", numPuerto);

            String jsonBody = mapper.writeValueAsString(objetoJson);
            // Create the request entity with the body and headers
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // Send the POST request and get the response entity
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity("http://"+ direccionIp +":"+ puerto +"/maestro/cargar", requestEntity, Void.class);

            // Check the response status code
             if (responseEntity.getStatusCode() == HttpStatus.OK)
                 System.out.println("POST request sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public Nodo consultar(String archivo) {

        Nodo nodoMaestro = maestros.get(0);

        try {
            // establish a connection with the Maestro node
            Socket socketMaestro = new Socket(nodoMaestro.getDireccionIp(), nodoMaestro.getPuerto());

            ObjectOutputStream outputStream = new ObjectOutputStream(socketMaestro.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socketMaestro.getInputStream());

            outputStream.writeObject(new Consulta(archivo));

            Object respuesta = inputStream.readObject();

            // Close the connection
            outputStream.close();
            socketMaestro.close();

            return (Nodo) respuesta;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    } */

    public List<String> listaArchivosDisponibles() {
        File carpetaArchivos = new File("C:\\Users\\leo_2\\OneDrive\\Documentos\\GitHub\\sistemas-distribuidos\\TP 2\\Ejercicio 1\\extreme\\archivos");
        File[] archivos = carpetaArchivos.listFiles();
        List<String> recursosPropios = new ArrayList<>();
        for (File archivo : archivos) {
            if (archivo.isFile()) {
                recursosPropios.add(archivo.getName());
            }
        }
        return recursosPropios;
    }

    public void consultarMaestro(String archivo) throws JsonProcessingException {
        
        String direccionIp = "localhost";
        int puerto = 8084;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://"+ direccionIp +":"+ puerto +"/maestro/consultar?archivo= " + archivo, String.class);

    }
    
}
