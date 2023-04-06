# ISO
FROM eclipse-temurin:19-jdk-alpine

COPY target/demo-0.0.1-SNAPSHOT.jar C:/Users/marco/Descargas/app.jar
WORKDIR C:/Users/marco/Descargas/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]