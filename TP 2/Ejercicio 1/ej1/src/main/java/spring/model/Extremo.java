package spring.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Extremo {
    private List<String> maestros;
    private Map<String, List<Recurso>> recursosPropios;

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

    public List<Recurso> obtenerRecursosDeMaestro(String nodoMaestro) {
        // Código para establecer conexión con el nodo maestro y obtener la lista de recursos disponibles
        // Ejemplo:
        try {
            Socket socketMaestro = new Socket(nodoMaestro, 1234);
            // Enviar consulta al nodo maestro
            // Recibir respuesta del nodo maestro con la lista de recursos disponibles
            // Cerrar conexión con el nodo maestro
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void mostrarRecursosDisponibles(List<Recurso> recursosDisponibles) {
        // Código para mostrar la lista de recursos disponibles al usuario
        // Ejemplo:
        System.out.println("Recursos disponibles:");
        for (Recurso recurso : recursosDisponibles) {
            System.out.println(recurso);
        }
    }

    public List<Recurso> seleccionarRecursos(List<Recurso> recursosDisponibles) {
        // Código para que el usuario seleccione los recursos a descargar
        // Ejemplo:
        List<Recurso> recursosSeleccionados = recursosDisponibles.subList(0, 1);
        return recursosSeleccionados;
    }

    private List<Par> buscarParesCorrespondientes(String nodoMaestro, List<Recurso> recursosSeleccionados) {
        List<Par> paresCorrespondientes = new ArrayList<>();
        try {
            // Crear conexión con el nodo maestro
            Socket socket = new Socket(nodoMaestro, 8000);
    
            // Enviar solicitud al nodo maestro
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("BUSCAR:" + recursosSeleccionados.toString());
    
            // Recibir respuesta del nodo maestro
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String respuesta = dis.readUTF();
            paresCorrespondientes = convertirMensajeAPares(respuesta);
    
            // Cerrar streams y socket
            dos.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paresCorrespondientes;
    }
    
    private List<Par> convertirMensajeAPares(String mensaje) {
        List<Par> pares = new ArrayList<>();
        // Separar el mensaje en líneas
        String[] lineas = mensaje.split("\n");
        for (String linea : lineas) {
            // Separar cada línea en partes
            String[] partes = linea.split(" ");
            if (partes.length >= 3) {
                // Tomar los primeros 3 elementos de la línea (dirección IP, puerto y nombre del archivo)
                String direccionIP = partes[0];
                int puerto = Integer.parseInt(partes[1]);
                String nombreArchivo = partes[2];
                // Agregar un nuevo Par con los datos extraídos
                pares.add(new Par(direccionIP, puerto, nombreArchivo));
            }
        }
        return pares;
    }
    

    private void descargarArchivos(List<Par> paresCorrespondientes) {
        // Código para descargar los archivos de los pares correspondientes
            for (Par par : paresCorrespondientes) {
                try {
                    // Conectarse con el nodo par correspondiente
                    Socket socket = new Socket(par.getDireccionIp(), par.getPuerto());
        
                    // Enviar solicitud al nodo par correspondiente
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF("DESCARGAR:" + par.getNombreArchivo());
        
                    // Recibir archivo del nodo par correspondiente
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    FileOutputStream fos = new FileOutputStream(par.getNombreArchivo());
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = dis.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
        
                    // Cerrar streams y socket
                    fos.close();
                    dos.close();
                    dis.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    // Para modo servidor
    public void atenderSolicitud(Solicitud solicitud) {
        // Código para buscar si se tienen los recursos requeridos y devolver la información correspondiente
    }
}
