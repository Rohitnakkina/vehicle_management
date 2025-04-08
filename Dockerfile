# Use a base Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar file into the container
COPY target/PrimeAutomobilesApplication-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on (as per application.properties)
EXPOSE 9090

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]