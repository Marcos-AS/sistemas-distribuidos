# POST create task
#curl -X POST http://localhost:8092/api/taskmanager/createTask -H 'Content-Type: application/json' -d '{"taskName":"add2num","fullContainerImage":"marcos1as/imagen-ej7", "apiPath": "/api/task/example", "methodPath":"/runTask", "parameters":{"numA":"10","numB":"25"}}'

curl -X POST http://34.172.171.172:8080/api/taskmanager/createTask -H 'Content-Type: application/json' -d '{"taskName":"add2num","fullContainerImage":"marcos1as/imagen-ej7", "parameters":{"numA":"10","numB":"25"}}'


#curl -X POST http://localhost:8080/api/task/example/runTask -H 'Content-Type: application/json' -d '{"numA": "10", "numB":"20"}'

#gcloud container clusters get-credentials primary --zone us-central1-a --project firm-source-399022