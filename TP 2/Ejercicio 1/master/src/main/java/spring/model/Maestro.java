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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class Maestro{
    private List<Nodo> nodosExtremos;
    private Map<Nodo, List<String>> recursosPorExtremo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Maestro() {
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

    public void cargar(@RequestBody MensajeListaArchivos mensaje) {
        try {
            String sql = "INSERT INTO extremo (direccionIp, puerto) VALUES (?, ?)";
            jdbcTemplate.update(sql, mensaje.getNodoExtremo().getDireccionIp(), mensaje.getNodoExtremo().getPuerto());
            System.out.println("INSERT realizado.");
            /*sql = "INSERT INTO archivo (nombre, magicNumber) VALUES (?, ?)";
            jdbcTemplate.update(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    String nombre = mensaje.getListaArchivos().get(i);
                    ps.setString(1, nombre);
                   // ps.setInt(2, magicNumber);
                }
                @Override
                public int getBatchSize() {
                    return mensaje.getListaArchivos().size();
                }
            });*/
            } catch (Exception e) {
                System.out.println(e);
            }   
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
