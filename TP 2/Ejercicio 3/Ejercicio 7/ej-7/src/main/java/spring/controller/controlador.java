package spring.controller;
import com.google.gson.Gson;
import com.google.auth.oauth2.GoogleCredentials;
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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/taskmanager")
public class controlador {
  int min = 8000;
  int max = 9999;
  private Gson gson = new Gson();

  @Value("${projectid}")
  private String projectId;

  @Autowired
  TaskRepository taskRepository;

  //método ejecutar tarea remota, las peticiones POST las maneja este método
  @PostMapping("/createTask")
  public ResponseEntity<?> executeRemoteTask(@RequestBody String taskJson) {
    //HttpResponse<String> response = null;
    try {
      CmdRunner cmdRunner = new CmdRunner();
      //HttpRequests httpManager = new HttpRequests();

      //1) deserialización a objeto TareaGenerica desde JSON
      TareaGenerica task = gson.fromJson(taskJson, TareaGenerica.class);
      System.out.println(task.getTaskName());
      String idTarea = UUID.randomUUID().toString();
      task.setId(idTarea);
      task.setEstado("EN PROCESO");
      taskRepository.save(task);

      //2) busca un puerto disponible (num aleatorio entre min y max)
      Boolean port_available = false;
			int randomNum = 8500;
			while (!port_available) {
				randomNum = ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
				
				//String result = cmdRunner.runCommand("C:/Users/leo_2/AppData/Local/Temp", "ss -tulpn | grep :"+randomNum+" | head -n1");
				String result = cmdRunner.runCommand("C:/Users/leo_2/AppData/Local/Temp", "netstat -ano | findstr :" + randomNum + " | findstr LISTENING | findstr /V 0.0.0.0");

        if (result.length() > 0){
          System.out.println("port not available, retrying");
        }else{
					port_available = true;
					System.out.println("Port "+randomNum+" free, let's start docker container");	
				}
				Thread.sleep(2000);
			}

      //3) levanta el contenedor

      String docker_container = "docker run --rm --name="+task.getTaskName()+"-"+randomNum+" -ti -d -p "+randomNum+":8080 "+ task.getFullContainerImage();
      cmdRunner.runCommand("C:/Users/leo_2/AppData/Local/Temp", docker_container);

      Thread.sleep(10000);
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