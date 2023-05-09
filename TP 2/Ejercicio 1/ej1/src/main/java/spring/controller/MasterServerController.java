package spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import spring.model.Maestro;
import spring.model.MensajeListaArchivos;
import spring.model.Nodo;
import spring.model.Recurso;

import java.util.List;

@RestController
@RequestMapping("/maestro")
public class MasterServerController {

    private Maestro maestro;

    public MasterServerController() {
        int puerto = 8081;
        String IP = "localhost";
        this.maestro = new Maestro(IP, puerto); 
    }

    @GetMapping("/recursos")
    @ResponseBody
    public List<Recurso> obtenerRecursosDisponibles() {
        return maestro.getRecursosDisponibles();
    }

    @PostMapping("/iniciar")
    public void iniciarMaestro(@RequestBody MensajeListaArchivos mensaje) {
        maestro.iniciar(mensaje);
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
