package spring.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Maestro extends Nodo{
    private List<Nodo> nodosExtremos;
    private Map<Nodo, List<String>> recursosPorExtremo;

    @Autowired
    public Maestro(String direccionIp, int puerto) {
        super(direccionIp, puerto);
        this.recursosPorExtremo = new HashMap<Nodo, List<String>>(); 
    }

    public void gestionarES() throws ClassNotFoundException, IOException {
        // Implementar la lógica para gestionar la E/S de los peers
        while (true) {
            // Esperar a recibir una consulta de un nodo extremo
            Consulta consulta = recibirConsulta();
    
            // Realizar la búsqueda correspondiente en los recursos disponibles en los nodos extremos
            List<Recurso> resultados = buscarRecursos(consulta);
    
            // Devolver los resultados al nodo extremo que hizo la consulta
           // enviarResultados(resultados, consulta.getDireccionIpNodoExtremo(), consulta.getPuertoNodoExtremo());
        }
    }

    public Consulta recibirConsulta() throws IOException, ClassNotFoundException {
        // Crear el socket del servidor y esperar a que llegue una conexión de un nodo extremo
        ServerSocket servidorSocket = new ServerSocket();
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
                Socket socket = new Socket(nodo.getDireccionIp(), nodo.getPuerto());
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
      //  return this.recursosDisponibles;
      return null;
    }

    public void actualizarRecursos(List<Recurso> recursos) {
      //  this.recursosDisponibles = recursos;
    }

    public void iniciar() {
        // start the Maestro node here
        // for example, you could create a new thread to handle incoming requests
        
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(getPuerto());

                    while (true) {
                        Socket socketExtremo = serverSocket.accept();
        
                        // Atender solicitud del extremo
                        ObjectInputStream inputStream = new ObjectInputStream(socketExtremo.getInputStream());
                        Object mensaje = inputStream.readObject();
        
                       /* if (mensaje instanceof Nodo) {
                            // Registrar nuevo extremo
                            Nodo nodoExtremo = (Nodo) mensaje;
                            nodosExtremos.add(nodoExtremo);
                            System.out.println("Se ha registrado el extremo " + nodoExtremo.getDireccionIp() + ":" + nodoExtremo.getPuerto()); */
                       if (mensaje instanceof MensajeListaArchivos) {
                            // Actualizar lista de archivos disponibles
                            MensajeListaArchivos nodoLista = (MensajeListaArchivos) mensaje;
                            recursosPorExtremo.put(nodoLista.getNodoExtremo(), nodoLista.getListaArchivos());
                            System.out.println("El extremo " + nodoLista.getNodoExtremo().getDireccionIp() + ":" + nodoLista.getNodoExtremo().getPuerto() + " ha compartido " + nodoLista.getListaArchivos().size() + " archivos.");
                        }
        
                        // Cerrar conexión
                        inputStream.close();
                        socketExtremo.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        serverThread.start();        
    }

    public void mostrarRecursos() {
        // Iteramos sobre las listas de cada clave
        for (Nodo clave : recursosPorExtremo.keySet()) {
            System.out.println("Clave: " + clave.getDireccionIp() + ": " + clave.getPuerto());
            List<String> lista = recursosPorExtremo.get(clave);
            for (String valor : lista) {
                System.out.println("Valor: " + valor);
            }
        }
    }
}
