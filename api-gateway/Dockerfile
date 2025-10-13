# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy parent pom and api-gateway pom
COPY pom.xml ./
COPY api-gateway/pom.xml ./api-gateway/

# Copy source code
COPY api-gateway/src ./api-gateway/src

# Build the application
RUN apk add --no-cache maven
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/api-gateway/target/*.jar app.jar

# Expose gateway port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
