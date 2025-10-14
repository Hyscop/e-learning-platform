package com.elearning.course.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lesson Model
 * 
 * Represents a single lesson within a module
 * This is an EMBEDDED document (not a separate collection)
 * Stored inside Module array in MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    
    /**
     * Lesson title
     */
    private String title;
    
    /**
     * Lesson description
     */
    private String description;
    
    /**
     * Video URL (if video lesson)
     * Example: "https://youtube.com/watch?v=..."
     */
    private String videoUrl;
    
    /**
     * Duration in seconds
     */
    private Integer duration;
    
    /**
     * Lesson order in module
     */
    private Integer orderIndex;
    
    /**
     * Is this lesson free preview?
     * Free lessons can be watched without enrolling
     */
    @Builder.Default
    private Boolean isFreePreview = false;
}