# Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Run the packaged application
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /build/target/pc-builder-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
