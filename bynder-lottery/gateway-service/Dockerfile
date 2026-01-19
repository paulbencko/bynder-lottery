FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/gateway-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
