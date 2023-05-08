package spring.model;

import java.util.List;

public class Solicitud {

    private String nombreArchivo;
    private String direccionIP;
    private int puerto;
    List<Recurso> recursosEncontrados;

    // constructor, getters y setters
    public Solicitud(List<Recurso> recursosEncontrados) {
    }

    public List<Recurso> getRecursosEncontrados() {
        return this.recursosEncontrados;
    }
}
