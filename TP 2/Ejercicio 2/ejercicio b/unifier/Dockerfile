FROM eclipse-temurin:19-jdk-alpine

COPY unifier/target/unifier-0.0.1-SNAPSHOT.jar /app/app.jar
COPY cloud/terraform/terraform.json /app/terraform.json
WORKDIR /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]