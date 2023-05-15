package spring.master.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class Maestro{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void cargar(String direccionIp, int puerto, List<String> archivos) {
        
        try {
            // Inserta extremo en la bd
            String sql = "INSERT INTO Extremo (direccionIp, puerto) VALUES (?, ?)";
            jdbcTemplate.update(sql, direccionIp, puerto);
            System.out.println("INSERT de extremo realizado.");

            // Inserta cada archivo en la BD
            sql = "INSERT INTO Archivo (direccionIp, puerto, nombre) VALUES (?, ?, ?)";
            for (String nombre : archivos) {
                jdbcTemplate.update(sql, direccionIp, puerto, nombre);
            }
            /*jdbcTemplate.update(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    String nombre = archivos.get(i);
                    ps.setString(1, direccionIp);
                    ps.setInt(2, puerto);
                    ps.setString(3, nombre);
                }
                @Override
                public int getBatchSize() {
                    return archivos.size();
                }
            }); */
            System.out.println("INSERT de archivos realizado.");
            } catch (Exception e) {
                 System.out.println(e);
            }   
        } 
    }

