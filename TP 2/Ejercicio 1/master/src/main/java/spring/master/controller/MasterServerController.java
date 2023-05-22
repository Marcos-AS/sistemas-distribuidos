package spring.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import spring.master.services.Maestro;

@RestController
@RequestMapping("/maestro")
public class MasterServerController {

    @Autowired
    private Maestro maestro;
        
    @PostMapping("/cargar")
    public void cargarExtremo(HttpServletRequest request, @RequestBody Map<String,Object> datosExtremo) throws SQLException {
        String puerto = (String) datosExtremo.get("puerto");
        List<String> archivos = (List<String>) datosExtremo.get("archivos");
        maestro.cargar(request.getRemoteAddr(), Integer.parseInt(puerto), archivos);
    }

    @GetMapping("/consultar")
    public String buscarArchivo(@RequestParam("archivo") String archivo) {
        System.out.println("Archivo recibido: " + archivo);
        return maestro.buscar(archivo);
    }
    
}
