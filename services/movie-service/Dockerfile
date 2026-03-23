# Stage 1: Build the application using the official Maven image
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy the ENTIRE cinemesh root project into the container
COPY . .

# Build the entire project to perfectly resolve all dependencies (Bulletproof method)
RUN mvn clean package -DskipTests

# Stage 2: Create the lightweight runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as non-root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Extract ONLY the movie-service jar from the massive build
COPY --from=builder /app/services/movie-service/target/*.jar app.jar

EXPOSE 8080

# K8s memory limits
ENTRYPOINT ["java", \
            "-XX:InitialRAMPercentage=50.0", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseContainerSupport", \
            "-jar", "/app/app.jar"]
