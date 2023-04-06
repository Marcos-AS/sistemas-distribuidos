#GET 
curl http://localhost:8080/api/taskmanager/listTasks/1

# POST create task
curl -X POST http://localhost:8080/api/taskmanager/createTask \
   -H 'Content-Type: application/json' \
   -d '{"clientID": "3", "taskName":"add2num","fullContainerImage":"dpetrocelli/demotask:latest", "apiPath": "/api/task/example", "methodPath":"/runTask", "parameters":{"numA":"10","numB":"25"}}'



// HashMap<String, String> parameters = new HashMap<String,String>();
		// parameters.put("key1", "param1");
		// parameters.put("key2","parameter2");

		// Task t = new Task("task","path", parameters);


curl -X POST http://localhost:8767/api/task/example/runTask \
    -H 'Content-Type: application/json' \
    -d '{"numA": "10", "numB":"20"}'
