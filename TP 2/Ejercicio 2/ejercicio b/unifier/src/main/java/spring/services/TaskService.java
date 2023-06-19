package spring.services;

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

        if (task != null) {
            task.get().setEstado("TERMINADO");
            taskRepository.save(task.get());
        }
    }

}