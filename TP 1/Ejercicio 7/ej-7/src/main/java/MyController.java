import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
//import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MyController {

  private static final String DOCKER_IMAGE_NAME = "my-docker-image";
  private static final String DOCKER_CONTAINER_NAME = "my-docker-container";
  private static final int DOCKER_CONTAINER_PORT = 8080;
  private static final String DOCKER_CONTAINER_CMD = "java -jar /my-app.jar";

  private DockerClient dockerClient = DockerClientBuilder.getInstance().build();
  private Gson gson = new Gson();

  @PostMapping("/executeRemoteTask")
  public ResponseEntity<String> executeRemoteTask(@RequestBody String taskJson) {
    TareaGenerica task = gson.fromJson(taskJson, TareaGenerica.class);

    // Crear un contenedor Docker
    CreateContainerResponse container = dockerClient.createContainerCmd(DOCKER_IMAGE_NAME)
        .withName(DOCKER_CONTAINER_NAME)
        .withExposedPorts(ExposedPort.tcp(DOCKER_CONTAINER_PORT))
        .withHostConfig(HostConfig.newHostConfig().withPortBindings(new PortBinding(PortBinding.parse(String.format("%d:%d", DOCKER_CONTAINER_PORT, DOCKER_CONTAINER_PORT)))).withAutoRemove(true))
        .exec();

    // Iniciar el contenedor Docker
    dockerClient.startContainerCmd(container.getId()).exec();

    // Ejecutar la tarea dentro del contenedor Docker
    ExecCreateCmdResponse cmd = dockerClient.execCreateCmd(container.getId())
        .withAttachStdout(true)
        .withAttachStderr(true)
        .withCmd("sh", "-c", DOCKER_CONTAINER_CMD + " '" + gson.toJson(task) + "'")
        .exec();
    dockerClient.execStartCmd(cmd.getId()).exec(new ExecStartResultCallback());

    // Esperar un momento para que la tarea se ejecute en el contenedor Docker
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Obtener el resultado de la tarea desde el contenedor Docker
    String result = dockerClient.logContainerCmd(container.getId()).withStdOut(true).exec().toString();

    // Eliminar el contenedor Docker
    dockerClient.removeContainerCmd(container.getId()).exec();

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  public static void main(String[] args) {
    SpringApplication.run(MyController.class, args);
  }

}

class MyData {
  private String field1;
  private int field2;
  
  // Getters y setters
  
}




//el cliente le manda una tarea en JSON y el servidor la convierte a un objeto Java 
//mediante Gson para poder manipular los datos. Luego, vuelve a convertir los datos a
//formato JSON para enviarle la tarea a un contenedor Docker para que la resuelva
//Para que el servidor pueda ejecutar tareas en un contenedor Docker, se necesitará una
// biblioteca Java que permita interactuar con Docker, como Docker Java.