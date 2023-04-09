FROM eclipse-temurin:19-jdk-alpine

COPY target/ej-1-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]