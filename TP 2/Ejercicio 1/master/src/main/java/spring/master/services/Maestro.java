package spring.master.services;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Maestro{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void cargar(String direccionIp, int puerto, List<String> archivos) {
        
        /*try {
            // Inserta extremo en la bd
            String sql = "INSERT INTO Extremo (direccionIp, puerto) VALUES (?, ?)";
            jdbcTemplate.update(sql, direccionIp, puerto);
            System.out.println("INSERT de extremo realizado.");

            // Inserta cada archivo en la BD
            sql = "INSERT INTO Archivo (direccionIp, puerto, nombre) VALUES (?, ?, ?)";
            for (String nombre : archivos) {
                jdbcTemplate.update(sql, direccionIp, puerto, nombre);
            }
            System.out.println("INSERT de archivos realizado.");
            } catch (Exception e) {
                 System.out.println(e);
            }   
        } */

        try {
            // Verificar si el extremo ya existe en la base de datos
            String extremoExistenteQuery = "SELECT COUNT(*) FROM Extremo WHERE direccionIp = ? AND puerto = ?";
            int extremoExistente = jdbcTemplate.queryForObject(extremoExistenteQuery, Integer.class, direccionIp, puerto);
            boolean extremoNuevo = (extremoExistente == 0);
    
            // Insertar extremo en la base de datos solo si es nuevo
            if (extremoNuevo) {
                String insertExtremoQuery = "INSERT INTO Extremo (direccionIp, puerto) VALUES (?, ?)";
                jdbcTemplate.update(insertExtremoQuery, direccionIp, puerto);
                System.out.println("INSERT de extremo realizado.");
            } else {
                System.out.println("El extremo ya existe en la base de datos.");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // Insertar archivos en la base de datos (solo los que no existen)
            String insertArchivoQuery = "INSERT INTO Archivo (direccionIp, puerto, nombre) VALUES (?, ?, ?)";
            for (String nombre : archivos) {
                try {
                    String archivoExistenteQuery = "SELECT COUNT(*) FROM Archivo WHERE direccionIp = ? AND puerto = ? AND nombre = ?";
                    int archivoExistente = jdbcTemplate.queryForObject(archivoExistenteQuery, Integer.class, direccionIp, puerto, nombre);
                    if (archivoExistente == 0) {
                        jdbcTemplate.update(insertArchivoQuery, direccionIp, puerto, nombre);
                        System.out.println("INSERT del archivo " + nombre + "realizado.");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        }
        

    public String buscar(String archivo) {
        
        String sql = "SELECT direccionIp, puerto FROM Archivo WHERE nombre = ?";
        Object[] params = {archivo};

        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, params);

        if (resultados.isEmpty()) {
            throw new NoSuchElementException("No se encontró ningún resultado para el archivo: " + archivo);
        }

        Map<String, Object> resultado = resultados.get(0);
        String direccionIp = (String) resultado.get("direccionIp");
        int puerto = (int) resultado.get("puerto");
        
        return direccionIp + ":" + puerto;
    } 
}

