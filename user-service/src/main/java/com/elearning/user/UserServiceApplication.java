package com.elearning.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * USER SERVICE - Main Application Entry Point
 * 
 * @SpringBootApplication combines three annotations:
 * 1. @Configuration - Marks this as a configuration class
 * 2. @EnableAutoConfiguration - Spring Boot auto-configures based on dependencies
 * 3. @ComponentScan - Scans for @Component, @Service, @Repository, @Controller
 * 
 * WHY? One annotation simplifies setup - Spring Boot's "convention over configuration"
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method - application entry point
     * 
     * Spring Boot:
     * 1. Starts embedded Tomcat server
     * 2. Scans for annotated classes
     * 3. Configures database connection
     * 4. Sets up REST endpoints
     * 5. Initializes security
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        
        // After startup, you'll see:
        // - Server running on port 8081
        // - Database connection established
        // - Available REST endpoints logged
        System.out.println("""
            
            ========================================
            🚀 User Service Started Successfully!
            ========================================
            📍 Port: 8081
            🔗 Base URL: http://localhost:8081/api/users
            💚 Health: http://localhost:8081/actuator/health
            ========================================
            """);
    }
}
