package com.elearning.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway Application
 * 
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);

        System.out.println("""

                ========================================
                🚀 API Gateway Started Successfully!
                ========================================
                📍 Port: 8080
                🔗 Base URL: http://localhost:8080/api/
                💚 Health: http://localhost:8080/actuator/health
                ========================================
                """);
    }
}
