# Enviamos imagen para dividir en fragmentos
#curl -X POST -F "file=@imgs/image.jpg" -F "numPieces=4" http://localhost:8080/divide-image
curl -X POST -F "file=@imgs/leo.jpg" -F "numPieces=4" http://34.172.110.4:8080/divide-image


# Comandos para despliegue en cloud

# Para archivos de deploy y service
    # kubectl apply -f <nombre-archivo> (Se debe estar parado en el directorio del archivo)

# Para eliminar pods, deployments y servicios
    # kubectl delete pod <nombre-pod>
    # kubectl delete deployment <nombre-deploy>
    # kubectl delete service <nombre-servicio>