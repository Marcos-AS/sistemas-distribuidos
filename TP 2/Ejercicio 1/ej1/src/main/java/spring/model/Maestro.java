package spring.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Maestro {
    private List<Nodo> nodosExtremos;
    public static final int PUERTO_MAESTRO = 8000;
    public List<Recurso> recursosDisponibles;

    public Maestro(List<Nodo> nodosExtremos) {
        this.nodosExtremos = nodosExtremos;
    }

    public void gestionarES() throws ClassNotFoundException, IOException {
        // Implementar la lógica para gestionar la E/S de los peers
        while (true) {
            // Esperar a recibir una consulta de un nodo extremo
            Consulta consulta = recibirConsulta();
    
            // Realizar la búsqueda correspondiente en los recursos disponibles en los nodos extremos
            List<Recurso> resultados = buscarRecursos(consulta);
    
            // Devolver los resultados al nodo extremo que hizo la consulta
            enviarResultados(resultados, consulta.getDireccionIpNodoExtremo(), consulta.getPuertoNodoExtremo());
        }
    }

    public Consulta recibirConsulta() throws IOException, ClassNotFoundException {
        // Crear el socket del servidor y esperar a que llegue una conexión de un nodo extremo
        ServerSocket servidorSocket = new ServerSocket(PUERTO_MAESTRO);
        Socket socket = servidorSocket.accept();
    
        // Leer la consulta del objeto enviado por el nodo extremo
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Consulta consulta = (Consulta) in.readObject();
    
        // Cerrar los streams y el socket
        in.close();
        socket.close();
        servidorSocket.close();
    
        return consulta;
    }

    public List<Recurso> buscarRecursos(Consulta consulta) {
        List<Recurso> recursosEncontrados = new ArrayList<>();
    
        // Iterar sobre los nodos extremos registrados en el servidor maestro
        for (Nodo nodo : nodosExtremos) {
            try {
                // Conectar con el nodo extremo y enviar la consulta
                Socket socket = new Socket(nodo.getDireccionIpNodoExtremo(), nodo.getPuertoNodoExtremo());
                ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                salida.writeObject(consulta);
    
                // Recibir la respuesta del nodo extremo
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                Solicitud solicitud = (Solicitud) entrada.readObject();
    
                // Agregar los recursos encontrados a la lista de resultados
                recursosEncontrados.addAll(solicitud.getRecursosEncontrados());
    
                // Cerrar la conexión con el nodo extremo
                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                // Manejar las excepciones
                e.printStackTrace();
            }
        }
    
        return recursosEncontrados;
    }

    public void enviarResultados(List<Recurso> resultados, String direccionIP, int puerto) {
        try {
            // Crear el socket para enviar los resultados al nodo extremo
            Socket socket = new Socket(direccionIP, puerto);
    
            // Crear el stream de salida para enviar los resultados
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
    
            // Escribir los resultados en el stream de salida
            out.writeObject(resultados);
    
            // Cerrar el stream y el socket
            out.close();
            socket.close();
        } catch (IOException e) {
            // Manejar la excepción en caso de error
            System.err.println("Error al enviar los resultados: " + e.getMessage());
        }
    } 
           
    // Métodos para añadir y quitar nodos extremos, getters y setters
    public void añadirNodoExtremo(Nodo nodoExtremo) {
        nodosExtremos.add(nodoExtremo);
    }
    
    public void quitarNodoExtremo(Nodo nodoExtremo) {
        nodosExtremos.remove(nodoExtremo);
    }

    public List<Recurso> getRecursosDisponibles() {
        return this.recursosDisponibles;
    }

    public void actualizarRecursos(List<Recurso> recursos) {
        this.recursosDisponibles = recursos;
    }

    public void iniciar() {
    }
}
