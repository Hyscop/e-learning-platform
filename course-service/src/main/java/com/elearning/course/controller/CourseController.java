package com.elearning.course.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elearning.course.model.Course;
import com.elearning.course.model.CourseLevel;
import com.elearning.course.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Course Controller
 * Base path: /api/courses
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    // ==================== CRUD Operations ====================

    /**
     * Create new course
     * POST /api/courses/create
     * ONLY INSTRUCTORS AND ADMINS can create courses
     */
    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody Course course,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-FirstName") String firstName,
            @RequestHeader("X-User-LastName") String lastName) {

        log.info("Create course request received from user: {} {} ({}) with role: {}", 
                firstName, lastName, userEmail, userRole);

        // Validate role - only INSTRUCTOR and ADMIN can create courses
        if (!"INSTRUCTOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            log.warn("User {} with role {} attempted to create a course - FORBIDDEN", userEmail, userRole);
            throw new SecurityException("Only instructors and admins can create courses");
        }

        // Set the instructor information from the authenticated user
        course.setInstructorEmail(userEmail);
        course.setInstructorFirstName(firstName);
        course.setInstructorLastName(lastName);

        Course created = courseService.createCourse(course);

        log.info("Course created successfully with ID: {} by instructor: {} {}", 
                created.getId(), firstName, lastName);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all courses
     * GET /api/courses/all
     */

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        log.info("Get all courses request received");
        List<Course> courses = courseService.getAllCourses();
        log.info("Returning {} courses", courses.size());
        return ResponseEntity.ok(courses);
    }

    /**
     * Get course by ID
     * GET /api/courses/details/{id}
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        log.info("Get course by ID request: {}", id);
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Update course
     * PUT /api/courses/update/{id}
     * Uses X-User-Email header injected by API Gateway
     * ONLY INSTRUCTORS AND ADMINS can update courses
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String id,
            @Valid @RequestBody Course course,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Update course request for ID: {} by user: {} with role: {}", id, userEmail, userRole);

        // Validate role - only INSTRUCTOR and ADMIN can update courses
        if (!"INSTRUCTOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            log.warn("User {} with role {} attempted to update course {} - FORBIDDEN", userEmail, userRole, id);
            throw new SecurityException("Only instructors and admins can update courses");
        }

        Course updated = courseService.updateCourse(id, course, userEmail);
        log.info("Course updated successfully: {}", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete course
     * DELETE /api/courses/delete/{id}
     * Uses X-User-Email header injected by API Gateway
     * ONLY INSTRUCTORS AND ADMINS can delete courses
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        log.warn("Delete course request for ID: {} by user: {} with role: {}", id, userEmail, userRole);

        // Validate role - only INSTRUCTOR and ADMIN can delete courses
        if (!"INSTRUCTOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            log.warn("User {} with role {} attempted to delete course {} - FORBIDDEN", userEmail, userRole, id);
            throw new SecurityException("Only instructors and admins can delete courses");
        }

        courseService.deleteCourse(id, userEmail);
        log.info("Course deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Course Management ====================

    /**
     * Toggle publish status
     * PATCH /api/courses/toggle-publish/{id}
     * ONLY INSTRUCTORS AND ADMINS can toggle publish status
     */
    @PatchMapping("/toggle-publish/{id}")
    public ResponseEntity<Course> togglePublishStatus(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Toggle publish status request for course: {} by user: {} with role: {}", id, userEmail, userRole);

        // Validate role - only INSTRUCTOR and ADMIN can toggle publish status
        if (!"INSTRUCTOR".equals(userRole) && !"ADMIN".equals(userRole)) {
            log.warn("User {} with role {} attempted to toggle publish status of course {} - FORBIDDEN", userEmail,
                    userRole, id);
            throw new SecurityException("Only instructors and admins can toggle publish status");
        }

        Course updated = courseService.togglePublishStatus(id, userEmail);
        log.info("Course {} publish status toggled to: {}", id, updated.getIsPublished());
        return ResponseEntity.ok(updated);
    }

    // ==================== Query Operations ====================

    /**
     * Get published courses only
     * GET /api/courses/published
     */
    @GetMapping("/published")
    public ResponseEntity<List<Course>> getPublishedCourses() {
        log.info("Get published courses request received");
        List<Course> courses = courseService.getPublishedCourses();
        log.info("Returning {} published courses", courses.size());
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by instructor
     * GET /api/courses/instructor/{instructorEmail}
     */
    @GetMapping("/instructor/{instructorEmail}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(
            @PathVariable String instructorEmail) {
        log.info("Get courses by instructor request: {}", instructorEmail);
        List<Course> courses = courseService.getCoursesByInstructor(instructorEmail);
        log.info("Returning {} courses for instructor: {}", courses.size(), instructorEmail);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by category
     * GET /api/courses/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(
            @PathVariable String category) {
        log.info("Get courses by category request: {}", category);
        List<Course> courses = courseService.getCoursesByCategory(category);
        log.info("Returning {} courses in category: {}", courses.size(), category);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses by level
     * GET /api/courses/level/{level}
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Course>> getCoursesByLevel(
            @PathVariable CourseLevel level) {
        log.info("Get courses by level request: {}", level);
        List<Course> courses = courseService.getCoursesByLevel(level);
        log.info("Returning {} courses with level: {}", courses.size(), level);
        return ResponseEntity.ok(courses);
    }

    /**
     * Search courses by title
     * GET /api/courses/search?title=Spring
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(
            @RequestParam String title) {
        log.info("Search courses request with title: {}", title);
        List<Course> courses = courseService.searchCoursesByTitle(title);
        log.info("Search returned {} courses matching: {}", courses.size(), title);
        return ResponseEntity.ok(courses);
    }

    // ==================== Utility Operations ====================

    /**
     * Check if course exists
     * GET /api/courses/exists/{id}
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> courseExists(@PathVariable String id) {
        log.debug("Check if course exists: {}", id);
        boolean exists = courseService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get total course count
     * GET /api/courses/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCourseCount() {
        log.info("Get total course count request received");
        long count = courseService.getTotalCourseCount();
        log.info("Total courses in system: {}", count);
        return ResponseEntity.ok(count);
    }
}
