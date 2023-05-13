package spring.model;

import java.io.Serializable;
import java.util.List;

public class MensajeListaArchivos implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Nodo nodoExtremo;
    private final List<String> listaArchivos;

    public MensajeListaArchivos(Nodo nodoExtremo, List<String> listaArchivos) {
        this.nodoExtremo = nodoExtremo;
        this.listaArchivos = listaArchivos;
    }

    public Nodo getNodoExtremo() {
        return nodoExtremo;
    }

    public List<String> getListaArchivos() {
        return listaArchivos;
    }
}

