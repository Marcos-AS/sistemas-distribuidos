#curl -X GET -F "nombreImagen=image.jpg"  http://localhost:8080/unified-image --output imagen.jpg
#curl -X GET -F "nombreImagen=leo.jpg"  http://34.172.110.4:8080/unified-image --output imgs/messi.jpg

# Realizar la solicitud GET y guardar la respuesta en un archivo
curl -s -o response.txt -w "%{http_code}" "http://34.172.110.4:8080/unified-image?nombreImagen=leo.jpg"

# Leer el código de estado de la respuesta
http_code=$(tail -n 1 response.txt)

# Verificar el código de estado y actuar en consecuencia
if [[ $http_code -eq 200 ]]; then
  # El código de estado es 200, se descargó la imagen correctamente
  mv response.txt output.jpg
else
  # El código de estado es diferente de 200, se produjo un error
  cat response.txt
fi

# Eliminar el archivo de respuesta temporal
rm response.txt
