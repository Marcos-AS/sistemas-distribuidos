package spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import spring.model.Maestro;
import spring.model.Nodo;
import spring.model.Recurso;

import java.util.List;

@Controller
@RequestMapping("/maestro")
public class MasterServerController {

    @Autowired
    private Maestro maestro;

    @GetMapping("/recursos")
    @ResponseBody
    public List<Recurso> obtenerRecursosDisponibles() {
        return maestro.getRecursosDisponibles();
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
