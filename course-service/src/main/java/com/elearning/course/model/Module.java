package com.elearning.course.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Module Model
 * 
 * This is an EMBEDDED document (not a separate collection)
 * Stored inside Course document in MongoDB
 * Contains an array of Lessons (also embedded)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    
    /**
     * Module title
     */
    private String title;
    
    /**
     * Module description
     */
    private String description;
    
    /**
     * Module order in course
     */
    private Integer orderIndex;
    
    /**
     * Lessons in this module
     * EMBEDDED ARRAY - Lessons are stored inside module
     * No separate collection, no joins needed!
     */
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();
}