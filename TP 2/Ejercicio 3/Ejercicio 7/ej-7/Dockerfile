# ISO
FROM eclipse-temurin:19-jdk-alpine

COPY target/webServer-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]