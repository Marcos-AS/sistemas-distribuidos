package spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MasterServerController  {
    
    @GetMapping("/resources")
    public List<String> getResources() {
        // Lógica para obtener los recursos disponibles en el servidor maestro
        List<String> resources = new ArrayList<>();
        resources.add("archivo1.txt");
        resources.add("archivo2.txt");
        return resources;
    }
}
