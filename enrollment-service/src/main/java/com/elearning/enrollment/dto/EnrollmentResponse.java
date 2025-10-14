package com.elearning.enrollment.dto;

import com.elearning.enrollment.model.Enrollment;
import com.elearning.enrollment.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private String id;
    private String studentEmail;
    private String studentFirstName;
    private String studentLastName;
    private String courseId;
    private String courseTitle;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private Integer progressPercentage;
    private LocalDateTime lastAccessDate;
    private LocalDateTime completionDate;

    public static EnrollmentResponse fromEntity(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudentEmail(),
                enrollment.getStudentFirstName(),
                enrollment.getStudentLastName(),
                enrollment.getCourseId(),
                enrollment.getCourseTitle(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getProgressPercentage(),
                enrollment.getLastAccessDate(),
                enrollment.getCompletionDate());
    }

    public static EnrollmentResponse fromEntity(Enrollment enrollment,
            String studentFirstName,
            String studentLastName,
            String courseTitle) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudentEmail(),
                studentFirstName,
                studentLastName,
                enrollment.getCourseId(),
                courseTitle,
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getProgressPercentage(),
                enrollment.getLastAccessDate(),
                enrollment.getCompletionDate());
    }
}
