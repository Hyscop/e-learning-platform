package com.elearning.enrollment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.elearning.enrollment.client.CourseServiceClient;
import com.elearning.enrollment.exception.DuplicateEnrollmentException;
import com.elearning.enrollment.exception.EnrollmentNotFoundException;
import com.elearning.enrollment.exception.InvalidProgressException;
import com.elearning.enrollment.model.Enrollment;
import com.elearning.enrollment.model.EnrollmentStatus;
import com.elearning.enrollment.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseServiceClient courseServiceClient;

    @CacheEvict(value = "enrollments", allEntries = true)
    public Enrollment enrollStudent(String studentEmail, String firstName, String lastName, String courseId) {
        log.info("Attempting to enroll student '{}' in course '{}'", studentEmail, courseId);

        if (enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId)) {
            log.warn("Duplicate enrollment attempt: student '{}' already enrolled in course '{}'", studentEmail,
                    courseId);
            throw new DuplicateEnrollmentException(studentEmail, courseId);
        }

        String courseTitle = courseServiceClient.getCourseTitle(courseId);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentEmail(studentEmail);
        enrollment.setStudentFirstName(firstName);
        enrollment.setStudentLastName(lastName);
        enrollment.setCourseId(courseId);
        enrollment.setCourseTitle(courseTitle);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgressPercentage(0);
        enrollment.setLastAccessDate(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Successfully enrolled student '{}' in course '{}' with enrollment ID '{}'",
                studentEmail, courseId, saved.getId());

        try {
            courseServiceClient.incrementEnrollmentCount(courseId);
            log.info("Incremented enrollment count for course '{}'", courseId);
        } catch (Exception e) {
            log.error("Failed to increment enrollment count for course '{}': {}", courseId, e.getMessage());
        }

        return saved;
    }

    @Cacheable(value = "enrollments", key = "'student:' + #studentEmail")
    public List<Enrollment> getStudentEnrollments(String studentEmail) {
        log.debug("Fetching enrollments for student '{}'", studentEmail);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentEmail(studentEmail);
        log.debug("Found {} enrollments for student '{}'", enrollments.size(), studentEmail);
        return enrollments;
    }

    @Cacheable(value = "enrollments", key = "'course:' + #courseId")
    public List<Enrollment> getCourseEnrollments(String courseId) {
        log.debug("Fetching enrollments for course '{}'", courseId);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        log.debug("Found {} enrollments for course '{}'", enrollments.size(), courseId);
        return enrollments;
    }

    @CacheEvict(value = "enrollments", allEntries = true)
    public Enrollment updateProgress(String studentEmail, String courseId, Integer progress) {
        log.info("Updating progress for student '{}' in course '{}' to {}%", studentEmail, courseId, progress);

        if (progress < 0 || progress > 100) {
            log.error("Invalid progress value: {}", progress);
            throw new InvalidProgressException(progress);
        }

        Enrollment enrollment = enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId)
                .orElseThrow(() -> {
                    log.error("Enrollment not found for student '{}' in course '{}'", studentEmail, courseId);
                    return new EnrollmentNotFoundException(studentEmail, courseId);
                });

        enrollment.setProgressPercentage(progress);
        enrollment.setLastAccessDate(LocalDateTime.now());

        if (progress >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletionDate(LocalDateTime.now());
            log.info("Student '{}' completed course '{}'", studentEmail, courseId);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        log.info("Successfully updated progress for student '{}' in course '{}'", studentEmail, courseId);

        return updated;
    }

    @CacheEvict(value = "enrollments", allEntries = true)
    public void dropEnrollment(String studentEmail, String courseId) {
        log.info("Attempting to drop enrollment for student '{}' in course '{}'", studentEmail, courseId);

        Enrollment enrollment = enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId)
                .orElseThrow(() -> {
                    log.error("Enrollment not found for student '{}' in course '{}'", studentEmail, courseId);
                    return new EnrollmentNotFoundException(studentEmail, courseId);
                });

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);

        try {
            courseServiceClient.decrementEnrollmentCount(courseId);
            log.info("Decremented enrollment count for course '{}'", courseId);
        } catch (Exception e) {
            log.error("Failed to decrement enrollment count for course '{}': {}", courseId, e.getMessage());
        }

        log.info("Successfully dropped enrollment for student '{}' in course '{}'", studentEmail, courseId);
    }

}
