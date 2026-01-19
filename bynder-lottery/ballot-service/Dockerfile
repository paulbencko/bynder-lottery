FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/ballot-service-1.0-SNAPSHOT-exec.jar app.jar

EXPOSE 8083 9093

ENTRYPOINT ["java", "-jar", "app.jar"]
