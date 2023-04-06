import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.google.gson.Gson;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class controlador {

  private static final String DOCKER_IMAGE_NAME = "my-docker-image";
  private static final String DOCKER_CONTAINER_NAME = "my-docker-container";
  private static final int DOCKER_CONTAINER_PORT = 8080;
  private static final String DOCKER_CONTAINER_CMD = "java -jar /my-app.jar";

  private DockerClient dockerClient = DockerClientBuilder.getInstance().build();
  private Gson gson = new Gson();

  //método ejecutar tarea remota, las peticiones POST las maneja este método
  @PostMapping("/executeRemoteTask")
  public ResponseEntity<?> executeRemoteTask(@RequestBody String taskJson) {
    String response = null;
    try {
      CmdRunner cmdRunner = new CmdRunner();
      HttpRequests httpManager = new HttpRequests();

      //1) deserialización a objeto TareaGenerica desde JSON
      TareaGenerica task = gson.fromJson(taskJson, TareaGenerica.class);

      // Crear un contenedor Docker
      CreateContainerResponse container = dockerClient.createContainerCmd(DOCKER_IMAGE_NAME)
      .withName(DOCKER_CONTAINER_NAME)
      .withExposedPorts(ExposedPort.tcp(DOCKER_CONTAINER_PORT))
      // .withHostConfig(HostConfig.newHostConfig().withPortBindings(new PortBinding(PortBinding.parse(String.format("%d:%d", DOCKER_CONTAINER_PORT, DOCKER_CONTAINER_PORT)))).withAutoRemove(true))
      .withPortBindings(PortBinding.parse(String.format("%d:%d", DOCKER_CONTAINER_PORT, DOCKER_CONTAINER_PORT)))
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
      Thread.sleep(5000);

      // Obtener el resultado de la tarea desde el contenedor Docker
      response = dockerClient.logContainerCmd(container.getId()).withStdOut(true).exec(null).toString();

      // Eliminar el contenedor Docker
      dockerClient.removeContainerCmd(container.getId()).exec();
    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    return new ResponseEntity<String>(response, HttpStatus.OK);
  }


}

			
/* codigo de la clase controlador del profesor
			// [STEP 1] - Once loaded, check port availability
			Boolean port_available = false;
			int randomNum = 0;
			while (!port_available) {
				randomNum = ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
				
				String result = cmdRunner.runCommand("/tmp/", "ss -tulpn | grep :"+randomNum+" | head -n1");
				log.info("result: "+result);
				if (result.length() > 0){
					log.error("Port "+randomNum+" used, retrying");	
				}else{
					port_available = true;
					log.info ("Port "+randomNum+" free, let's start docker container");	
				}
				Thread.sleep(2000);
			}
			
			// [STEP 2] - Once port obtained, start docker container
			String docker_container = "docker run --rm --name="+t.getTaskName()+"-"+randomNum+" -p "+randomNum+":8080 "+ t.getFullContainerImage()+" &";
			cmdRunner.runCommand("/tmp/", docker_container);

			// [STEP 3] - Once container running, send http request ç
			Thread.sleep (5000);
			log.info ("send packet");
			String ip = "127.0.0.1";
			String url = "http://"+ip+":"+randomNum+t.getApiPath()+t.getMethodPath();
			log.info ("URL:"+url);
      //crea un cliente que envía una petición POST
			response = httpManager.PostHttpRequest(url, t.getParameters());
			


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 */

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