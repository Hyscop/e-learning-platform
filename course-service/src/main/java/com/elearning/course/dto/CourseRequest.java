package com.elearning.course.dto;

import com.elearning.course.model.CourseLevel;
import com.elearning.course.model.Module;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Request DTO
 * 
 * Used for creating and updating courses
 * Contains validation annotations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Course description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @NotBlank(message = "Instructor email is required")
    @Email(message = "Invalid email format")
    private String instructorEmail;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or positive")
    private BigDecimal price;

    @NotBlank(message = "Language is required")
    private String language;

    private String thumbnailUrl;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    @Builder.Default
    private Boolean isPublished = false;
}