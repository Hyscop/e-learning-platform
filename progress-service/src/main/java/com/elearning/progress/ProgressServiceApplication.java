package com.elearning.progress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
public class ProgressServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProgressServiceApplication.class, args);

          System.out.println("""
            
            ========================================
            🚀 User Service Started Successfully!
            ========================================
            📍 Port: 8084
            🔗 Base URL: http://localhost:8084/api/progress
            💚 Health: http://localhost:8084/actuator/health
            ========================================
            """);
    }
}