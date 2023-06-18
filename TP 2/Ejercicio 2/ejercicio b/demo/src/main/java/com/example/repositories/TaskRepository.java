package com.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.models.Task;

public interface TaskRepository extends JpaRepository<Task, String> {
}