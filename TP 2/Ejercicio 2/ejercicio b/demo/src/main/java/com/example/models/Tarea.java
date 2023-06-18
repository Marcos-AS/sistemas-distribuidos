package com.example.models;

import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Tarea {
    @Id
    private String id;

    private String estado;
    private int cantPartes;
    private Time tiempo_inicio;
    private Time tiempo_fin;

    // Constructor, getters y setters
}