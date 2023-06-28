#curl -X GET -F "nombreImagen=image.jpg"  http://localhost:8080/unified-image --output imagen.jpg
#curl -X GET -F "nombreImagen=leo.jpg"  http://34.172.110.4:8080/unified-image --output imgs/messi.jpg

#!/bin/bash

url="http://35.224.36.56:8080/unified-image"
taskId="924a2436-f87b-4435-8780-eec665923915"
outputFile="output.jpg"

# Realizar la solicitud GET y guardar la respuesta en un archivo
response=$(curl -s -w "%{http_code}" -o "$outputFile" "$url?idTarea=$taskId")

# Extraer el código de estado de la respuesta
httpCode=${response: -3}

# Verificar el código de estado y actuar en consecuencia
if [[ $httpCode -eq 200 ]]; then
    # El código de estado es 200, se descargó la imagen correctamente
    echo "Imagen descargada correctamente"
elif [[ $httpCode -eq 202 ]]; then
    # El código de estado es 202, el servidor continua procesando
    echo "La imagen se encuentra en procesamiento..."
else
    # El código de estado es diferent e de 200, se produjo un error
    echo "Error al descargar la imagen. Código de estado: $httpCode"
fi

