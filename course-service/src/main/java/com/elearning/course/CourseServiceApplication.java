package com.elearning.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Course Service Application
 * 
 */
@SpringBootApplication
@EnableMongoAuditing
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);

        System.out.println("""

                ========================================
                🚀 Course Service Started Successfully!
                ========================================
                📍 Port: 8082
                🔗 Base URL: http://localhost:8082/api/courses
                💚 Health: http://localhost:8082/actuator/health
                ========================================
                """);
    }
}
