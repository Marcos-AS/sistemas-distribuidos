package spring.extreme.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletResponse;
import spring.extreme.services.Extremo;
import spring.extreme.services.MaestroRequest;

import com.fasterxml.jackson.annotation.JsonInclude;

@RestController
@RequestMapping("/extremo")
public class ExtremeServerController {

    @Autowired
    private Extremo extremo;

    //@Value("${server.port}")
    //private String numPuerto;
        
    @PostMapping("/informar")
    public void informar(@RequestBody MaestroRequest maestroRequest) {
        extremo.informarMaestro(maestroRequest.getDireccionIp(), maestroRequest.getPuerto());
    }

    @GetMapping("/descargar")
    public void descargar(@RequestParam("archivo") String archivo) throws IOException {
        extremo.consultarMaestro(archivo);
    }

    @GetMapping("/get")
    public ResponseEntity<Resource> enviarArchivo(@RequestParam("archivo") String archivo /*HttpServletResponse response*/) {
        return extremo.enviarArchivo(archivo);
    }
}





    


