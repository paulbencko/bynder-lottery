FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/participant-service-1.0-SNAPSHOT-exec.jar app.jar

EXPOSE 8081 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
