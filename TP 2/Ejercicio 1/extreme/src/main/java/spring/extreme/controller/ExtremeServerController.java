package spring.extreme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import spring.extreme.model.Extremo;
import spring.extreme.model.MaestroRequest;

@RestController
@RequestMapping("/extremo")
public class ExtremeServerController {

    @Autowired
    private Extremo extremo;
        
    @PostMapping("/informar")
    public void informar(@RequestBody MaestroRequest maestroRequest) {
        extremo.informarMaestro(maestroRequest.getDireccionIp(), maestroRequest.getPuerto());
    }

    @GetMapping("/descargar")
    public void descargar(@RequestParam("archivo") String archivo) {
        System.out.println("Archivo recibido: " + archivo);
        try {
            extremo.consultarMaestro(archivo);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @GetMapping("/get")
    public void enviarArchivo(@RequestParam("archivo") String archivo) {
        
    }



    

}
