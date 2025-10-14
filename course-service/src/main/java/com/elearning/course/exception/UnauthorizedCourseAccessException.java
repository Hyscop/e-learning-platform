package com.elearning.course.exception;

/**
 * Unauthorized Course Access Exception
 */
public class UnauthorizedCourseAccessException extends RuntimeException {

    public UnauthorizedCourseAccessException(String message) {
        super(message);
    }

    public UnauthorizedCourseAccessException(String courseId, String instructorEmail) {
        super("Instructor " + instructorEmail + " is not authorized to access course " + courseId);
    }
}