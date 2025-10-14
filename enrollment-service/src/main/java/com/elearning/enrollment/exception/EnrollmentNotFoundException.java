package com.elearning.enrollment.exception;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException(String studentEmail, String courseId) {
        super(String.format("Enrollment not found for student '%s' in course '%s'", studentEmail, courseId));
    }
}
