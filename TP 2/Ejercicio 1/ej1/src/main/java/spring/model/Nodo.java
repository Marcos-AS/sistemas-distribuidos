package spring.model;

import java.io.Serializable;

public class Nodo implements Serializable{
    private String direccionIp;
    private int puerto;

    public Nodo(String direccionIp, int puerto) {
        this.direccionIp = direccionIp;
        this.puerto = puerto;
    }

    // Constructor, getters y setters

    public String getDireccionIp() {
        return this.direccionIp;
    }

    public int getPuerto() {
        return this.puerto;
    }
}
