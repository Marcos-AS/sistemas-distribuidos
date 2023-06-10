# Enviamos imagen para dividir en fragmentos
#curl -X POST -F "file=@imgs/image.jpg" -F "numPieces=4" http://localhost:8080/divide-image
curl -X POST -F "file=@imgs/leo.jpg" -F "numPieces=4" http://localhost:8080/divide-image