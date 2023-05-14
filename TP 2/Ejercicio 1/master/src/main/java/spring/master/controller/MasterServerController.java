package spring.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import spring.master.model.Maestro;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/maestro")
public class MasterServerController {

    @Autowired
    private Maestro maestro;
        
    @PostMapping("/cargar")
    public void cargarExtremo(HttpServletRequest request, @RequestBody List<String> archivos) throws SQLException {
        maestro.cargar(request.getRemoteAddr(), request.getRemotePort(), archivos);
    }
    
}
