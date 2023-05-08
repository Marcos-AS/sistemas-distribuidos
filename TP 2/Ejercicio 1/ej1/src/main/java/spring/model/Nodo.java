package spring.model;

import java.util.List;

public class Nodo {
    private String direccionIp;
    private int puerto;
    private List<Recurso> recursos;

    // Constructor, getters y setters

    public String getDireccionIpNodoExtremo() {
        return this.direccionIp;
    }

    public int getPuertoNodoExtremo() {
        return this.puerto;
    }
}
