package com.example.models;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="task")
public class Task {
    
    @Id
    @Column(name="id")
    private String id;

    @Column(name="estado")
    private String estado;

    @Column(name="cantidad_partes")
    private int cantPartes;

    @Column(name="tiempo_inicio")
    private LocalTime tiempo_inicio;

    @Column(name="tiempo_fin")
    private LocalTime tiempo_fin;

    // Constructor, getters y setters
}