import com.google.gson.Gson;
import java.util.concurrent.ThreadLocalRandom;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class controlador {
  int min = 8000;
  int max = 9999;
  private Gson gson = new Gson();

  //método ejecutar tarea remota, las peticiones POST las maneja este método
  @PostMapping("/executeRemoteTask")
  public ResponseEntity<?> executeRemoteTask(@RequestBody String taskJson) {
    HttpResponse<String> response = null;
    try {
      CmdRunner cmdRunner = new CmdRunner();
      HttpRequests httpManager = new HttpRequests();

      //1) deserialización a objeto TareaGenerica desde JSON
      TareaGenerica task = gson.fromJson(taskJson, TareaGenerica.class);

      //2) busca un puerto disponible (num aleatorio entre min y max)
      Boolean port_available = false;
			int randomNum = 0;
			while (!port_available) {
				randomNum = ThreadLocalRandom.current().nextInt(this.min, this.max + 1);
				
				String result = cmdRunner.runCommand("/tmp/", "ss -tulpn | grep :"+randomNum+" | head -n1");
				if (result.length() > 0){
          System.out.println("port not available, retrying");
        }else{
					port_available = true;
					System.out.println("Port "+randomNum+" free, let's start docker container");	
				}
				Thread.sleep(2000);
			}

      //3) levanta el contenedor
      String docker_container = "docker run --rm --name="+task.getTaskName()+"-"+randomNum+" -p "+randomNum+":8080 "+ task.getFullContainerImage()+" &";
      cmdRunner.runCommand("/tmp/", docker_container);


      Thread.sleep(5000);
      String ip = "127.0.0.1";
			String url = "http://"+ip+":"+randomNum+task.getApiPath()+task.getMethodPath();
			//4) crea un cliente que envía una petición POST
			response = httpManager.PostHttpRequest(url, task.getParameters());
  
    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    return new ResponseEntity<String>("Tarea almacenada " + response.body().trim(), HttpStatus.CREATED);
  }


}

//el cliente le manda una tarea en JSON y el servidor la convierte a un objeto Java 
//mediante Gson para poder manipular los datos. Luego, vuelve a convertir los datos a
//formato JSON para enviarle la tarea a un contenedor Docker para que la resuelva
//Para que el servidor pueda ejecutar tareas en un contenedor Docker, se necesitará una
// biblioteca Java que permita interactuar con Docker, como Docker Java.