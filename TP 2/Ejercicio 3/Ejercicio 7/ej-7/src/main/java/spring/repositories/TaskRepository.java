package spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.model.TareaGenerica;

public interface TaskRepository extends JpaRepository<TareaGenerica, String> {
}