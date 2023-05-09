package spring.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Extremo extends Nodo{
    private List<Maestro> maestros;
    private List<String> recursosPropios;
    private String rutaArchivos;

    public Extremo(List<Maestro> maestros, String direccinIp, int puerto, String rutaArchivos) {
        super(direccinIp, puerto);
        this.maestros = maestros;
        this.recursosPropios = new ArrayList<String>();
        this.rutaArchivos = rutaArchivos;
    }

    public void iniciar() {
        // Establecer conexión con el nodo maestro
        Nodo nodoMaestro = maestros.get(0); // Tomamos el primer nodo maestro
       
        informarMaestro(nodoMaestro);
    }

    private void informarMaestro(Nodo nodoMaestro) {
        
        try {
            // establish a connection with the Maestro node
            Socket socketMaestro = new Socket(nodoMaestro.getDireccionIp(), nodoMaestro.getPuerto());

            // Send a message to register the Extremo in the list of nodes
            ObjectOutputStream outputStream = new ObjectOutputStream(socketMaestro.getOutputStream());
           // outputStream.writeObject(new Nodo(this.getDireccionIp(), this.getPuerto()));

            // Send a list of available files to share
            outputStream.writeObject(new MensajeListaArchivos(new Nodo(this.getDireccionIp(), this.getPuerto()), listaArchivosDisponibles()));

            // Close the connection
            outputStream.close();
            socketMaestro.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Nodo consultar(String archivo) {

        Maestro nodoMaestro = maestros.get(0);

        try {
            // establish a connection with the Maestro node
            Socket socketMaestro = new Socket(nodoMaestro.getDireccionIp(), nodoMaestro.getPuerto());

            ObjectOutputStream outputStream = new ObjectOutputStream(socketMaestro.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socketMaestro.getInputStream());

            outputStream.writeObject(new Consulta(archivo));

            Object respuesta = inputStream.readObject();

            // Close the connection
            outputStream.close();
            socketMaestro.close();

            return (Nodo) respuesta;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> listaArchivosDisponibles() {
        File carpetaArchivos = new File(rutaArchivos);
        File[] archivos = carpetaArchivos.listFiles();
        for (File archivo : archivos) {
            if (archivo.isFile()) {
                recursosPropios.add(archivo.getName());
            }
        }
        return recursosPropios;
    }
    
    public List<Recurso> obtenerRecursosDeMaestro(Nodo nodoMaestro) {
        List<Recurso> recursos = new ArrayList<>();
    
        try {
            // establish a connection with the Maestro node
            Socket socketMaestro = new Socket(nodoMaestro.getDireccionIp(), nodoMaestro.getPuerto());
    
            // send a request to the Maestro node for the list of available resources
            OutputStream out = socketMaestro.getOutputStream();
            out.write("GET /maestro/recursos HTTP/1.1\r\n".getBytes());
            out.write(("Host: " + nodoMaestro + "\r\n").getBytes());
            out.write("Connection: close\r\n".getBytes());
            out.write("\r\n".getBytes());
    
            // read the response from the Maestro node and parse the list of resources
            InputStream in = socketMaestro.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean headersRead = false;
            while ((line = reader.readLine()) != null) {
                if (!headersRead) {
                    // skip the HTTP response headers
                    if (line.isEmpty()) {
                        headersRead = true;
                    }
                } else {
                    // parse the list of resources from the response body
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<List<Recurso>>(){}.getType();
                    recursos = gson.fromJson(line, type);
                }
            }
    
            // close the connection with the Maestro node
            socketMaestro.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return recursos;
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

    private List<Par> buscarParesCorrespondientes(Nodo nodoMaestro, List<Recurso> recursosSeleccionados) {
        List<Par> paresCorrespondientes = new ArrayList<>();
        try {
            // Crear conexión con el nodo maestro
            Socket socket = new Socket(nodoMaestro.getDireccionIp(), nodoMaestro.getPuerto());
    
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
