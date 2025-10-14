package com.elearning.enrollment.controller;

import com.elearning.enrollment.dto.EnrollmentRequest;
import com.elearning.enrollment.dto.EnrollmentResponse;
import com.elearning.enrollment.dto.ProgressUpdateRequest;
import com.elearning.enrollment.model.Enrollment;
import com.elearning.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @RequestHeader("X-User-Email") String studentEmail,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader(value = "X-User-FirstName", required = false) String firstName,
            @RequestHeader(value = "X-User-LastName", required = false) String lastName,
            @Valid @RequestBody EnrollmentRequest request) {

        log.info("Enrollment request from user '{}' with role '{}'", studentEmail, role);

        if (!"STUDENT".equals(role)) {
            log.warn("Non-student user '{}' attempted to enroll", studentEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Enrollment enrollment = enrollmentService.enrollStudent(studentEmail, firstName, lastName,
                request.getCourseId());
        EnrollmentResponse response = EnrollmentResponse.fromEntity(enrollment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @RequestHeader("X-User-Email") String studentEmail) {

        log.info("Fetching enrollments for user '{}'", studentEmail);

        List<EnrollmentResponse> enrollments = enrollmentService.getStudentEnrollments(studentEmail)
                .stream()
                .map(EnrollmentResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getCourseEnrollments(@PathVariable String courseId) {
        log.info("Fetching enrollments for course '{}'", courseId);

        List<EnrollmentResponse> enrollments = enrollmentService.getCourseEnrollments(courseId)
                .stream()
                .map(EnrollmentResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrollments);
    }

    @PutMapping("/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @RequestHeader("X-User-Email") String studentEmail,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody ProgressUpdateRequest request) {

        log.info("Progress update request from user '{}' for course '{}'", studentEmail, request.getCourseId());

        if (!"STUDENT".equals(role)) {
            log.warn("Non-student user '{}' attempted to update progress", studentEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Enrollment enrollment = enrollmentService.updateProgress(
                studentEmail,
                request.getCourseId(),
                request.getProgress());

        EnrollmentResponse response = EnrollmentResponse.fromEntity(enrollment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/drop")
    public ResponseEntity<Void> dropEnrollment(
            @RequestHeader("X-User-Email") String studentEmail,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody EnrollmentRequest request) {

        log.info("Drop enrollment request from user '{}' for course '{}'", studentEmail, request.getCourseId());

        if (!"STUDENT".equals(role)) {
            log.warn("Non-student user '{}' attempted to drop enrollment", studentEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        enrollmentService.dropEnrollment(studentEmail, request.getCourseId());
        return ResponseEntity.noContent().build();
    }
}
