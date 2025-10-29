# syntax=docker/dockerfile:1.7

# -------- Builder stage: compile the Spring Boot application with Gradle --------
FROM eclipse-temurin:22-jdk-alpine AS build

# Define working directory and Gradle cache location
WORKDIR /workspace
ENV GRADLE_USER_HOME=/workspace/.gradle

# Copy Gradle wrapper and build metadata first to leverage Docker layer caching
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle ./
COPY buildSrc buildSrc
COPY application/build.gradle.kts application/build.gradle.kts
COPY domain/build.gradle.kts domain/build.gradle.kts
COPY infrastructure/build.gradle.kts infrastructure/build.gradle.kts

# Ensure Gradle wrapper is executable
RUN chmod +x gradlew

# Copy the remaining project files
COPY . .

# Build the Bootable JAR and persist the Gradle cache between builds
RUN --mount=type=cache,target=/workspace/.gradle ./gradlew --no-daemon :infrastructure:bootJar


# -------- Runtime stage: run the Spring Boot application with a slim JRE --------
FROM eclipse-temurin:22-jre-alpine AS runtime

# Create and switch to a non-root user for better security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the working directory for the application runtime
WORKDIR /opt/app

# Copy the generated JAR from the builder stage into the runtime image
COPY --from=build /workspace/infrastructure/build/libs/*.jar /opt/app/application.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Launch the application
ENTRYPOINT ["java", "-jar", "/opt/app/application.jar"]
