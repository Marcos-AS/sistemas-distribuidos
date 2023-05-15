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
    public void consultar(@RequestParam("archivo") String archivo) throws JsonProcessingException {
        extremo.consultarMaestro(archivo);
    }



    

}
