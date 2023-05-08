package spring.model;

import java.util.List;

public class Par {
    private String direccionIP;
    private int puerto;
    private List<Recurso> recursos;

    // constructor, getters y setters

    public void agregarRecurso(Recurso recurso) {
        recursos.add(recurso);
    }

    public void atenderSolicitud(Solicitud solicitud) {
        // revisar si la solicitud coincide con algún recurso disponible
        // si es así, enviar el recurso solicitado al nodo que lo pidió
        // si no, enviar un mensaje de error o algo similar
    }
}
