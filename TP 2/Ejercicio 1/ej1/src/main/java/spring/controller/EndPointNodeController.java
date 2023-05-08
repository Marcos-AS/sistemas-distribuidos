package spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class EndPointNodeController {
   
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/query/{keyword}")
    public List<String> query(@PathVariable String keyword) {
        // Lógica para realizar una consulta en el servidor maestro
        List<String> resources = restTemplate.getForObject("http://master-server/resources", List.class);
        List<String> matchingResources = new ArrayList<>();
        for (String resource : resources) {
            if (resource.contains(keyword)) {
                matchingResources.add(resource);
            }
        }
        return matchingResources;
    }

    @GetMapping("/download/{resource}")
    public String download(@PathVariable String resource) {
        // Lógica para descargar un archivo de otro nodo extremo
        return restTemplate.getForObject("http://peer-node/download/" + resource, String.class);
    }
}
