package com.elearning.enrollment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.elearning.enrollment.model.Enrollment;
import com.elearning.enrollment.model.EnrollmentStatus;

public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    // Find all enrollments for a student
    List<Enrollment> findByStudentEmail(String studentEmail);

    // Find all students enrolled in a course
    List<Enrollment> findByCourseId(String courseId);

    // Check if student is already enrolled in a course
    boolean existsByStudentEmailAndCourseId(String studentEmail, String courseId);

    // Find specific enrollment
    Optional<Enrollment> findByStudentEmailAndCourseId(String studentEmail, String courseId);

    // Find enrollements by status
    List<Enrollment> findByStudentEmailAndStatus(String studentEmail, EnrollmentStatus status);

    long countByCourseId(String courseId);

}
