package com.elearning.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Enable Redis caching
public class EnrollmentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrollmentServiceApplication.class, args);

        System.out.println("""

                ========================================
                🚀 Enrollment Service Started Successfully!
                ========================================
                📍 Port: 8083
                🔗 Base URL: http://localhost:8083/api/enrollment
                💚 Health: http://localhost:8083/actuator/health
                ========================================
                """);
    }
}
