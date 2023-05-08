package spring.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Consulta {
    private String consulta;
    public String direccionIpExtremo;
    public int puertoExtremo;

    public Consulta(String consulta) {
        this.consulta = consulta;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getDireccionIpNodoExtremo() {
        return this.direccionIpExtremo;
    }

    public int getPuertoNodoExtremo() {
        return this.puertoExtremo;
    }


    
    
}
