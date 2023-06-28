package spring.services;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.models.Task;
import spring.repositories.TaskRepository;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public void updateState(String idTarea) {
        
        Optional<Task> task = taskRepository.findById(idTarea);

        if (task.isPresent()) {
            task.get().setEstado("TERMINADO");
            task.get().setTiempo_fin(LocalTime.now());
            taskRepository.save(task.get());
        }
    }

}