#curl -X POST -H "Content-Type: application/json" -d '{"direccionIp":"maestro", "puerto":8085}' http://localhost:5001/extremo/informar

curl -X GET -H "Content-Type: application/json" http://localhost:5002/extremo/descargar?archivo=1.txt