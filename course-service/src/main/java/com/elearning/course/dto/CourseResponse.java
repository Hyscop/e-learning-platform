package com.elearning.course.dto;

import com.elearning.course.model.CourseLevel;
import com.elearning.course.model.Module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Response DTO
 * 
 * Used for returning course data to clients
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private String id;
    private String title;
    private String description;
    private String instructorEmail;
    private String category;
    private CourseLevel level;
    private BigDecimal price;
    private String language;
    private String thumbnailUrl;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    private Boolean isPublished;
    private Integer enrollmentCount;
    private Double averageRating;
    private Integer reviewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
