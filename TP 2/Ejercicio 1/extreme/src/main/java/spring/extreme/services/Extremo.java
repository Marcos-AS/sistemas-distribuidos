package spring.extreme.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.core.Response;

@Component
public class Extremo {

    //para enviar peticiones
    RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port}")
    private String numPuerto;

    // curl -X POST -H "Content-Type: application/json" -d '{"direccionIp":"192.168.1.1", "puerto": 8080}' http://<direccion_ip>:<puerto>

    public ResponseEntity<String> informarMaestro(String direccionIp, int puerto) {
        
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

            RestTemplate restTemplate = new RestTemplate();
            System.out.println(direccionIp);
            System.out.println(puerto);
            System.out.println(requestEntity);
            // Send the POST request and get the response entity
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity("http://"+ direccionIp +":"+ puerto +"/maestro/cargar", requestEntity, Void.class);

            // Check the response status code
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("POST request sent successfully");
                return ResponseEntity.ok("Informacion enviada correctamente al maestro.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la información al maestro."); 
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la información al maestro.");
    }

    public List<String> listaArchivosDisponibles() {
        File carpetaArchivos = new File("/app/archivos");
        File[] archivos = carpetaArchivos.listFiles();
        List<String> recursosPropios = new ArrayList<>();
        for (File archivo : archivos) {
            if (archivo.isFile()) {
                recursosPropios.add(archivo.getName());
            }
        }
        return recursosPropios;
    }

    public ResponseEntity<String> consultarMaestro(String archivo) throws IOException {
        
        System.out.println("Archivo recibido: " + archivo);

        String direccionIp = "maestro";
        int puerto = 8085;

        RestTemplate restTemplate = new RestTemplate();

        // Consulta al maestro sobre el archivo recibido para saber con que extremo debe comunicarse
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://"+direccionIp+":"+puerto+"/maestro/consultar?archivo=" + archivo, String.class);
        //ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://2ad2-2800-2165-d000-10b-585f-dbab-9954-9723.sa.ngrok.io/maestro/consultar?archivo=" + archivo, String.class);
        System.out.println(responseEntity);
        
        String direccionExtremo = responseEntity.getBody();

        System.out.println(direccionExtremo);

        RestTemplate restTemplate2 = new RestTemplate();   

        ResponseEntity<byte[]> responseEntityExtreme = restTemplate2.getForEntity("http://"+direccionExtremo+"/extremo/get?archivo=" + archivo, byte[].class);

        // Verificar el código de respuesta
        HttpStatusCode statuscode = responseEntityExtreme.getStatusCode();
        if (statuscode.value() == 200) {

            // Guardar el archivo descargado como un objeto MultipartFile
            MultipartFile file = new MockMultipartFile("file", responseEntityExtreme.getBody());
            Path filepath = Paths.get("/app/archivos", archivo);

            Files.write(filepath, file.getBytes());

            System.out.println("Archivo descargado correctamente");

            //Ahora vamos a notificar al maestro que tengo mi archivo

            informarMaestro(direccionIp, puerto);

            return ResponseEntity.ok("Se descargo el archivo " + archivo + " correctamente en el extremo.");

        } else {
            System.out.println("Error al descargar el archivo. Código de respuesta: " + statuscode);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al descargar el archivo.");
        }
    }

    public ResponseEntity<Resource> enviarArchivo(String archivo) {
        // Obtener el archivo del servidor a partir del nombre
        File file = new File("/app/archivos/" + archivo);

        // Verificar si el archivo existe
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Crear un recurso de Spring para el archivo
        Resource recurso = new FileSystemResource(file);

        System.out.println("Enviando archivo.. :" + recurso.getFilename());
        // Crear una respuesta HTTP con el archivo adjunto
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(recurso);
    }
}
