# Enviamos imagen para dividir en fragmentos
curl -X POST -F "file=@imgs/leo.jpg" -F "numPieces=4" http://34.30.89.147:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/image.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/leo.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/image.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image
# curl -X POST -F "file=@imgs/guason.jpg" -F "numPieces=4" http://35.238.181.59:8080/divide-image


#curl -X POST -F "file=@guason.jpg" -F "numPieces=4" http://34.30.153.169:8080/divide-image


# Comandos para despliegue en cloud

# Para archivos de deploy y service
    # kubectl apply -f <nombre-archivo> (Se debe estar parado en el directorio del archivo)

# Para eliminar pods, deployments y servicios
    # kubectl delete pod <nombre-pod>
    # kubectl delete deployment <nombre-deploy>
    # kubectl delete service <nombre-servicio>

# gcloud container clusters get-credentials CLUSTER_NAME --zone ZONE --project PROJECT_ID
# gcloud container clusters get-credentials primary --zone us-central1-a --project able-tide-388304
