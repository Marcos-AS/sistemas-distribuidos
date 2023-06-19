package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
}