package task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;


import task.model.Task;

@Component
public class TasksController {
	Gson gson = new Gson();	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MessageConverter messageConverter;

	@RabbitListener(queues = {"task-queue"})
	public void processMessage(Message rabbitMessage){
		try {
			// [STEP 0] - Receive Task request and convert into Java Object
			// Task t = gson.fromJson(task, Task.class);
			byte[] mensaje = (byte[])this.messageConverter.fromMessage(rabbitMessage);
			String jsonString = new String(mensaje, StandardCharsets.UTF_8);
			JSONObject json = new JSONObject(jsonString);
			String taskId = json.getString("messageId");
			String taskName = json.getString("taskName");

			// Convertir el objeto JSON "parameters" a un HashMap
			JSONObject parametersJson = json.getJSONObject("parameters");
			HashMap<String, String> parameters = new HashMap<>();

			// Iterar sobre las claves del objeto JSON "parameters" y agregarlas al HashMap
			for (String key : parametersJson.keySet()) {
				String value = parametersJson.getString(key);
				parameters.put(key, value);
			}
			Task t = new Task(Integer.parseInt(parameters.get("numA")), Integer.parseInt(parameters.get("numB")));
			int result = t.addMethod();	
			log.info ("remote job result: "+result);
			System.out.println("resultado: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
}
