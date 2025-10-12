# USER SERVICE DOCKERFILE
# Multi-stage build: smaller final image, faster builds

# STAGE 1: BUILD
# WHY Maven image? Has Maven + JDK pre-installed
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory inside container
WORKDIR /app

# OPTIMIZATION: Copy only POM files first
# WHY? Docker caches layers - if POMs don't change, dependencies won't re-download
COPY pom.xml .
COPY user-service/pom.xml user-service/

# Download dependencies (cached if POMs unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY user-service/src user-service/src

# Build the application
# -DskipTests: skip tests in Docker build (run tests before building image)
# WHY? Faster builds, tests should run in CI/CD
RUN mvn clean package -DskipTests -pl user-service -am

# STAGE 2: RUNTIME
# WHY separate stage? JRE is smaller than JDK (no compiler needed in production)
FROM eclipse-temurin:21-jre-alpine

# Create non-root user for security
# WHY? Running as root is security risk
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy JAR from build stage
# WHY --from=build? Gets file from previous stage
COPY --from=build /app/user-service/target/*.jar app.jar

# Expose port (documentation - doesn't actually open port)
EXPOSE 8081

# Health check (Docker will monitor if container is healthy)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application
# WHY java -jar? Standard way to run Spring Boot fat JARs
ENTRYPOINT ["java", "-jar", "app.jar"]
