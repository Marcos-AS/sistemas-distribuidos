package spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import spring.model.Maestro;
import spring.model.MensajeListaArchivos;
import spring.model.Nodo;
import spring.model.Recurso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/maestro")
public class MasterServerController {

    @Autowired
    private Maestro maestro;
        
    public MasterServerController() {
    }

    @GetMapping("/recursos")
    @ResponseBody
    public List<Recurso> obtenerRecursosDisponibles() {
        return maestro.getRecursosDisponibles();
    }

    @PostMapping("/informar")
    public void cargarExtremo(@RequestBody MensajeListaArchivos mensaje) throws SQLException {
        maestro.cargar(mensaje);
    }
    
    @PostMapping("/registrar")
    @ResponseBody
    public void registrarNodo(@RequestParam Nodo nodo) {
        maestro.añadirNodoExtremo(nodo);
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public void actualizarRecursos(@RequestBody List<Recurso> recursos) {
        maestro.actualizarRecursos(recursos);
    }

    @PostMapping("/eliminar")
    @ResponseBody
    public void eliminarNodo(@RequestParam Nodo nodo) {
        maestro.quitarNodoExtremo(nodo);
    }

}
