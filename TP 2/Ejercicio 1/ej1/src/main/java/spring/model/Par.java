package spring.model;

import java.util.List;

public class Par {
    private String direccionIP;
    private int puerto;
    private String nombreArchivo;

    // constructor, getters y setters

    public Par(String direccionIP, int puerto, String nombreArchivo) {
        this.direccionIP = direccionIP;
        this.puerto = puerto;
        this.nombreArchivo = nombreArchivo;
    }

    public String getDireccionIp() {
        return this.direccionIP;
    }

    public int getPuerto() {
        return this.puerto;
    }

    public String getNombreArchivo() {
        return this.nombreArchivo;
    }

    public void atenderSolicitud(Solicitud solicitud) {
        // revisar si la solicitud coincide con algún recurso disponible
        // si es así, enviar el recurso solicitado al nodo que lo pidió
        // si no, enviar un mensaje de error o algo similar
    }
}
