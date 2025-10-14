# USER SERVICE DOCKERFILE

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory inside container
WORKDIR /app

# Copy parent POM and ALL module POMs
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY course-service/pom.xml course-service/
COPY enrollment-service/pom.xml enrollment-service/
COPY progress-service/pom.xml progress-service/
COPY api-gateway/pom.xml api-gateway/

# Copy only this service's source code
COPY user-service/src user-service/src

# Build the application (skip dependency resolution as it will happen during build)
RUN mvn clean package -DskipTests -pl user-service -am

# STAGE 2: RUNTIME

FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy JAR from build stage

COPY --from=build /app/user-service/target/*.jar app.jar

# Expose port (documentation - doesn't actually open port)
EXPOSE 8081

# Health check (Docker will monitor if container is healthy)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application

ENTRYPOINT ["java", "-jar", "app.jar"]
