#curl -X POST -H "Content-Type: application/json" -d '{"direccionIp":"maestro", "puerto":8085}' http://localhost:5001/extremo/informar


# Comando para cargar los archivos del extremo 1 en la bd a través del maestro
#curl -X POST -H "Content-Type: application/json" -d '{"direccionIp":"maestro", "puerto":8085}' https://34a5-181-169-198-82.ngrok-free.app/extremo/informar


# Comando para descargar el archivo 1.txt en el extremo 2 (con docker)
#curl -X GET -H "Content-Type: application/json" http://localhost:5002/extremo/descargar?archivo=1.txt

# Comando para descargar el archivo 1.txt en el extremo 2 (con ngrok)
curl -X GET -H "Content-Type: application/json" https://02df-181-169-198-82.ngrok-free.app/extremo/descargar?archivo=1.txt
