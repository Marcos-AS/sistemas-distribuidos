package spring.model;

public class Nodo {
    private String direccionIp;
    private int puerto;

    public Nodo(String direccionIp, int puerto) {
        this.direccionIp = direccionIp;
        this.puerto = puerto;
    }
    // Constructor, getters y setters

    public String getDireccionIpNodoExtremo() {
        return this.direccionIp;
    }

    public int getPuertoNodoExtremo() {
        return this.puerto;
    }
}
