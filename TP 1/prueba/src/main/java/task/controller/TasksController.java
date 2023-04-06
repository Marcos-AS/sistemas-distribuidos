package task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import task.model.Task;

@RestController
@RequestMapping(value = "/api/task/example")
public class TasksController {
	Gson gson = new Gson();	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@PostMapping("/runTask")
	public ResponseEntity<?> create (@RequestBody String task){
		
        // [STEP 0] - Receive Task request and convert into Java Object
		Task t = gson.fromJson(task, Task.class);
		int result = t.addMethod();	
		log.info ("remote job result: "+result);
		
        return new ResponseEntity<Integer>(result, HttpStatus.CREATED);
    }
}
