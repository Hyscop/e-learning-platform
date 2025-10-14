package com.elearning.enrollment.exception;

public class DuplicateEnrollmentException extends RuntimeException {
    public DuplicateEnrollmentException(String studentEmail, String courseId) {
        super(String.format("Student '%s' is already enrolled in course '%s'", studentEmail, courseId));
    }
}
