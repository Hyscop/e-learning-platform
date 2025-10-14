package com.elearning.course.exception;

/**
 * Course Not Found Exception
 * 
 * Thrown when a course is not found by ID
 * Will be handled by GlobalExceptionHandler to return 404
 */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(String id) {
        super("Course not found with id: " + id);
    }
}