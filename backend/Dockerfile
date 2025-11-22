# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy the Maven wrapper and pom.xml for dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies to cache them
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# Copy the rest of the application source code
COPY src/ src/

# Build the application
RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:25-jre-ubi10-minimal

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the Spring Boot application runs on
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
