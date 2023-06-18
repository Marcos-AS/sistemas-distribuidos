package com.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.models.Tarea;

public interface TaskRepository extends JpaRepository<Tarea, String> {
}