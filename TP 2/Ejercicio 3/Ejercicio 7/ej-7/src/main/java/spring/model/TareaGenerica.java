package spring.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="task")
public class TareaGenerica implements Serializable {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="taskName")
    private String taskName;
    
    @Column(name="estado")
    private String estado;

    private String fullContainerImage;

   // private String apiPath;

   // private String methodPath;

   /* @Column(name="tiempo_inicio")
    private LocalTime tiempo_inicio;

    @Column(name="tiempo_fin")
    private LocalTime tiempo_fin; */

    private HashMap<String, String> parameters = new HashMap<String, String>();

}
