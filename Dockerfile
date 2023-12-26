# Stage 1: Construir la aplicación con Maven
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests
RUN mv /app/target/*.jar /app/target/app.jar

# Stage 2: Ejecutar la aplicación
FROM amazoncorretto:17.0.0-alpine
WORKDIR /app
COPY --from=build /app/target/app.jar .
CMD ["java", "-jar", "app.jar"]
