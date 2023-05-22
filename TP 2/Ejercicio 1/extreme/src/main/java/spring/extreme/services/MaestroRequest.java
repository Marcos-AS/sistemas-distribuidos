package spring.extreme.services;

public class MaestroRequest {
    private String direccionIp;
    private int puerto;

    public MaestroRequest(String direccionIp, int puerto) {
        this.direccionIp = direccionIp;
        this.puerto = puerto;
    }

    public String getDireccionIp() {
        return this.direccionIp;
    }    
    
    public int getPuerto() {
        return this.puerto;
    }

}
