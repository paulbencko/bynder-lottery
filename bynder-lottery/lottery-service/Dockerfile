FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/lottery-service-1.0-SNAPSHOT-exec.jar app.jar

EXPOSE 8082 9092

ENTRYPOINT ["java", "-jar", "app.jar"]
