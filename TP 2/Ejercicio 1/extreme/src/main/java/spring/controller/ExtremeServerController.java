package spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import spring.model.Extremo;
import spring.model.MaestroRequest;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/extremo")
public class ExtremeServerController {

    @Autowired
    private Extremo extremo;
        
    //public ExtremeServerController() {
    //}
    
    @PostMapping("/informar")
    public void informar(@RequestParam MaestroRequest maestroRequest) {
        extremo.informarMaestro(maestroRequest.getDireccionIp(), maestroRequest.getPuerto());
    }

    

}
