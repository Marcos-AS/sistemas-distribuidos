package com.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseHandler {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public DatabaseHandler() {
        // Crea el EntityManagerFactory utilizando la configuración definida en persistence.xml
        entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public <T> void insert(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }

    public <T> List<T> getByAttribute(Class<T> entityClass, String attributeName, Object attributeValue) {
        String queryString = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + attributeName + " = :attributeValue";
        TypedQuery<T> query = entityManager.createQuery(queryString, entityClass);
        query.setParameter("attributeValue", attributeValue);
        return query.getResultList();
    }

    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
