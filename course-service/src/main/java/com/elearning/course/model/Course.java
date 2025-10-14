package com.elearning.course.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Model - MongoDB Document
 * 
 * Main entity representing a course in the system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

    /**
     * MongoDB ObjectId
     */
    @Id
    private String id;

    /**
     * Course title
     */
    private String title;

    /**
     * Course description
     */
    private String description;

    /**
     * Instructor Email (from User Service)
     */
    private String instructorEmail;

    /**
     * Instructor's first name
     */
    private String instructorFirstName;

    /**
     * Instructor's last name
     */
    private String instructorLastName;

    /**
     * Course category
     */
    private String category;

    /**
     * Course difficulty level
     */
    private CourseLevel level;

    /**
     * Course price
     */
    private BigDecimal price;

    /**
     * Course language
     */
    private String language;

    /**
     * Course thumbnail/image URL
     * Example: "https://cdn.example.com/courses/java.jpg"
     */
    private String thumbnailUrl;

    /**
     * Search tags for the course
     * Example: ["java", "programming", "beginner", "oop"]
     */
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Course modules (chapters/sections)
     * 
     */
    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    /**
     * Is course published and visible to students?
     */
    @Builder.Default
    private Boolean isPublished = false;

    /**
     * Total enrolled students count
     * Denormalized data from Enrollment Service
     */
    @Builder.Default
    private Integer enrollmentCount = 0;

    /**
     * Average rating (1-5)
     * Denormalized data from Review Service (future)
     * TODO will be dealt after review service
     */
    @Builder.Default
    private Double averageRating = 0.0;

    /**
     * Total reviews count
     */
    @Builder.Default
    private Integer reviewCount = 0;

    /**
     * Creation timestamp
     * 
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     * 
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}