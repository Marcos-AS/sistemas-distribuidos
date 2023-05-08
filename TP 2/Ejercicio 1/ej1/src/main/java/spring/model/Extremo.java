package spring.model;

import java.util.List;

public class Extremo {
    private List<String> maestros;

    public Extremo(List<String> maestros) {
        this.maestros = maestros;
    }

    public void iniciar() {
        // Establecer conexión con el nodo maestro
        String nodoMaestro = maestros.get(0); // Tomamos el primer nodo maestro
        List<Recurso> recursosDisponibles = obtenerRecursosDeMaestro(nodoMaestro);

        // Mostrar lista de recursos disponibles al usuario
        mostrarRecursosDisponibles(recursosDisponibles);

        // El usuario selecciona los recursos a descargar
        List<Recurso> recursosSeleccionados = seleccionarRecursos(recursosDisponibles);

        // Enviar solicitud al nodo maestro con la información de los recursos seleccionados
        List<Par> paresCorrespondientes = buscarParesCorrespondientes(nodoMaestro, recursosSeleccionados);

        // Descargar archivos de los pares correspondientes
        descargarArchivos(paresCorrespondientes);
    }

    private List<Recurso> obtenerRecursosDeMaestro(String nodoMaestro) {
        return null;
        // Código para establecer conexión con el nodo maestro y obtener la lista de recursos disponibles
    }

    private void mostrarRecursosDisponibles(List<Recurso> recursosDisponibles) {
        // Código para mostrar la lista de recursos disponibles al usuario
    }

    private List<Recurso> seleccionarRecursos(List<Recurso> recursosDisponibles) {
        return recursosDisponibles;
        // Código para que el usuario seleccione los recursos a descargar
    }

    private List<Par> buscarParesCorrespondientes(String nodoMaestro, List<Recurso> recursosSeleccionados) {
        return null;
        // Código para enviar solicitud al nodo maestro y obtener la información de contacto de los pares correspondientes
    }

    private void descargarArchivos(List<Par> paresCorrespondientes) {
        // Código para descargar los archivos de los pares correspondientes
    }

    // Para modo servidor
    public void atenderSolicitud(Solicitud solicitud) {
        // Código para buscar si se tienen los recursos requeridos y devolver la información correspondiente
    }
}
