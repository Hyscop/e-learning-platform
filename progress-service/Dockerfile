# ====================================
# Dockerfile for Progress Service
# ====================================
# Multi-stage build:
# Stage 1: Build with Maven
# Stage 2: Run with JRE (smaller image)

# ===== BUILD STAGE =====
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy parent POM and ALL module POMs
COPY pom.xml ./
COPY user-service/pom.xml ./user-service/
COPY course-service/pom.xml ./course-service/
COPY enrollment-service/pom.xml ./enrollment-service/
COPY progress-service/pom.xml ./progress-service/
COPY api-gateway/pom.xml ./api-gateway/

# Copy only this service's source code
COPY progress-service/src ./progress-service/src

# Build the application
RUN mvn clean package -DskipTests -pl progress-service -am

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/progress-service/target/*.jar app.jar

# Expose port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8084/api/progress/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: JVM tuning
# ENTRYPOINT ["java", \
#   "-Xms512m", \
#   "-Xmx1024m", \
#   "-XX:+UseG1GC", \
#   "-jar", "app.jar"]
