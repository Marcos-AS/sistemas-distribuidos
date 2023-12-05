# POST create task
#curl -X POST http://localhost:8092/api/taskmanager/createTask -H 'Content-Type: application/json' -d '{"taskName":"add2num","fullContainerImage":"marcos1as/imagen-ej7", "apiPath": "/api/task/example", "methodPath":"/runTask", "parameters":{"numA":"10","numB":"25"}}'

curl -X POST http://34.136.179.145:8080/api/taskmanager/createTask -H 'Content-Type: application/json' -d '{"taskName":"add2num","fullContainerImage":"marcos1as/imagen-ej7", "parameters":{"numA":"10","numB":"25"}}'


#curl -X POST http://localhost:8080/api/task/example/runTask -H 'Content-Type: application/json' -d '{"numA": "10", "numB":"20"}'