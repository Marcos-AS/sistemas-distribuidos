gcloud container clusters get-credentials primary --zone us-central1-a --project firm-source-399022
cd mariadb/k8s
kubectl apply -f deploy-bd.yaml
kubectl apply -f service-bd.yaml
cd ../../rabbitmq/k8s
kubectl apply -f deploy-rabbit.yaml
kubectl apply -f service-rabbit.yaml
cd ../../Ejercicio\ 7/k8s
kubectl apply -f deploy-webserver.yaml
kubectl apply -f service-load-balancer.yaml
#cd ../../prueba/k8s
#kubectl apply -f deploy-worker.yaml
#kubectl apply -f service-worker.yaml
kubectl get pod
kubectl get service #get external-ip