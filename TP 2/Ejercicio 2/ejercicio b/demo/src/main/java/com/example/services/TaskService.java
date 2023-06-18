package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.models.Task;
import com.example.repositories.TaskRepository;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public Task saveTask(Task user) {
        return taskRepository.save(user);
    }

}
