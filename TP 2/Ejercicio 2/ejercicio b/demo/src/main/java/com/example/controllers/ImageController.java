package com.example.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.services.TaskService;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
public class ImageController {

    @Autowired
    private TaskService taskService;

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @PostMapping("/divide-image")
    public ResponseEntity<String> divideImage(@RequestParam("file") MultipartFile file, @RequestParam("numPieces") int numPieces) throws FileNotFoundException, IOException {

        try {

            logger.debug(
                String.format(
                    "Se ejecuta el método divide. [file = %s]",
                    file.toString()
                )
            );

            return taskService.divideImage(file, numPieces);

        } catch (IOException e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cargar la imagen :("); 
        
        }

    }

    @GetMapping("/unified-image")
    public ResponseEntity<?> unifiedImage(@RequestParam("idTarea") String idTarea) throws IOException {
        
        try {

            logger.debug(
                String.format(
                    "Se ejecuta el método unifiedImage. [idTarea = %s]",
                    idTarea
                )
            );

            return taskService.unifiedImage(idTarea);

        } catch (IOException e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al procesar la imagen");
        
        }  
    }
}
