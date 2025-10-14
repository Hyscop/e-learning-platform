# COURSE SERVICE DOCKERFILE
# Multi-stage build for smaller final image

# STAGE 1: BUILD
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory inside container
WORKDIR /app

# OPTIMIZATION: Copy only POM files first
COPY pom.xml .
COPY course-service/pom.xml course-service/

# Download dependencies (cached if POMs unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY course-service/src course-service/src

# Build the application
RUN mvn clean package -DskipTests -pl course-service -am

# STAGE 2: RUNTIME
FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/course-service/target/*.jar app.jar

# Expose port (documentation - doesn't actually open port)
EXPOSE 8082

# Health check (Docker will monitor if container is healthy)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
