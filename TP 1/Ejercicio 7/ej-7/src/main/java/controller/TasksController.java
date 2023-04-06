package controller;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import model.TareaGenerica;
import model.Task;

@RestController
@RequestMapping(value = "/api/task/example")
public class TasksController {
	Gson gsonManager = new Gson();
	// @Autowired
	// RabbitMQSender rabbitMQSender;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@PostMapping("/runTask")
	public ResponseEntity<?> create (@RequestBody String task){
		
        // [STEP 0] - Receive Task request and convert into Java Object
		Task t = gsonManager.fromJson(task, Task.class);
		int result = t.addMethod();		
        return new ResponseEntity<Integer>(result, HttpStatus.CREATED);
    }
}
