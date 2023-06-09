package spring.extreme.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import spring.extreme.services.Extremo;
import spring.extreme.services.MaestroRequest;


@RestController
@RequestMapping("/extremo")
public class ExtremeServerController {

    @Autowired
    private Extremo extremo;
    
    @PostMapping("/informar")
    public ResponseEntity<String> informar(@RequestBody MaestroRequest maestroRequest) {
        return extremo.informarMaestro(maestroRequest.getDireccionIp(), maestroRequest.getPuerto());
    }

    @GetMapping("/descargar")
    public ResponseEntity<String> descargar(@RequestParam("archivo") String archivo) throws IOException {
        return extremo.consultarMaestro(archivo);
    }

    @GetMapping("/get")
    public ResponseEntity<Resource> enviarArchivo(@RequestParam("archivo") String archivo /*HttpServletResponse response*/) {
        return extremo.enviarArchivo(archivo);
    }
}