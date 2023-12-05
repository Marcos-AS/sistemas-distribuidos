package spring.controller;
import com.google.gson.Gson;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import spring.model.TareaGenerica;
import spring.repositories.TaskRepository;
import spring.services.CmdRunner;
import spring.services.HttpRequests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apps.*;


@RestController
@RequestMapping(value = "/api/taskmanager")
public class controlador {

  private Gson gson = new Gson();

  @Value("${projectid}")
  private String projectId;

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  //método ejecutar tarea remota, las peticiones POST las maneja este método
  @PostMapping("/createTask")
  public ResponseEntity<?> executeRemoteTask(@RequestBody String taskJson) {
    //HttpResponse<String> response = null;

    try {
     //HttpRequests httpManager = new HttpRequests();
     
     //1) deserialización a objeto TareaGenerica desde JSON
      TareaGenerica task = gson.fromJson(taskJson, TareaGenerica.class);
      System.out.println("Nombre de la tarea: " + task.getTaskName());
      String idTarea = UUID.randomUUID().toString();
      task.setId(idTarea);
      task.setEstado("EN PROCESO");
      taskRepository.save(task);

      // Crea objeto JSON
      JSONObject json = new JSONObject();
      json.put("messageId", idTarea);
      json.put("parameters", task.getParameters());
      json.put("taskName", task.getTaskName());

      // Convertir el objeto JSON a bytes
      byte[] jsonBytes = json.toString().getBytes(StandardCharsets.UTF_8);

      // Enviar el mensaje a la cola de RabbitMQ
      rabbitTemplate.convertAndSend("task-queue", jsonBytes);

      Thread.sleep(10000);

    /* try (KubernetesClient k8sCli = new KubernetesClientBuilder().build()) {
          String deploymentName = "deployment-worker";
          Deployment deployment = k8sCli.apps().deployments().inNamespace("default").withName(deploymentName).get();
          int currentReplicas = deployment.getSpec().getReplicas();
          k8sCli.apps().deployments().inNamespace("default").withName(deploymentName).scale(currentReplicas+1);
      } catch (KubernetesClientException e) {
        e.printStackTrace();
      } */

      // Importa las clases necesarias

    // Crea un cliente de Kubernetes
    try (KubernetesClient client = new DefaultKubernetesClient()) {
        // Define los detalles del Pod
        Pod pod = new PodBuilder()
                .withNewMetadata().withName("deployment-worker").endMetadata()
                .withNewSpec()
                .addNewContainer().withName("worker").withImage("leoduville5/tp2-ej3-worker:latest").endContainer()
                .endSpec()
                .build();

    // Crea el Pod en el clúster
    client.pods().create(pod);
}



      /*String ip = "127.0.0.1";
      //apiPath = api/task/example
			String url = "http://"+ip+":"+randomNum+task.getApiPath()+task.getMethodPath();
      //4) crea un cliente que envía una petición POST
			response = httpManager.PostHttpRequest(url, task.getParameters()); */
  
    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    return new ResponseEntity<String>("Tarea almacenada. Deberia devolver el ID de la tarea. ", HttpStatus.CREATED);
}

  public Storage inicializarCloud() throws FileNotFoundException, IOException {

      // Ruta del archivo JSON de las credenciales
      String rutaCredenciales = "/app/terraform.json";
      GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(rutaCredenciales));
  
      // Crea una instancia de StorageOptions con las credenciales y el ID del proyecto
      StorageOptions storageOptions = StorageOptions.newBuilder()
      .setCredentials(credentials)
      .setProjectId(projectId)
      .build();
  
      // Obtiene una instancia de Storage desde StorageOptions
      Storage storage = storageOptions.getService();

      return storage;
  
  }

}