# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy parent POM and ALL module POMs
COPY pom.xml ./
COPY user-service/pom.xml ./user-service/
COPY course-service/pom.xml ./course-service/
COPY enrollment-service/pom.xml ./enrollment-service/
COPY progress-service/pom.xml ./progress-service/
COPY api-gateway/pom.xml ./api-gateway/

# Copy only this service's source code
COPY api-gateway/src ./api-gateway/src

# Build the application
RUN mvn clean package -DskipTests -pl api-gateway -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/api-gateway/target/*.jar app.jar

# Expose gateway port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
