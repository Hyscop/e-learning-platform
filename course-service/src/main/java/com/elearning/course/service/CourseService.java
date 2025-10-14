package com.elearning.course.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.elearning.course.exception.CourseNotFoundException;
import com.elearning.course.exception.UnauthorizedCourseAccessException;
import com.elearning.course.model.Course;
import com.elearning.course.model.CourseLevel;
import com.elearning.course.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Course Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * Create a new course
     */
    public Course createCourse(Course course) {
        log.info("Creating a new course: {}", course.getTitle());

        course.setEnrollmentCount(0);
        course.setAverageRating(0.0);
        course.setReviewCount(0);

        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with id: {}", savedCourse.getId());

        return savedCourse;

    }

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() {
        log.info("Fetching all courses");
        return courseRepository.findAll();
    }

    /**
     * Get course by Id
     */
    public Course getCourseById(String id) {
        log.debug("Fetching course with id: {}", id);
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    /**
     * Get courses by instructor email
     */
    public List<Course> getCoursesByInstructor(String instructorEmail) {
        log.debug("Fetching courses for instructor: {}", instructorEmail);
        return courseRepository.findByInstructorEmail(instructorEmail);
    }

    /**
     * Get courses by category
     */
    public List<Course> getCoursesByCategory(String category) {
        log.debug("Fetching courses in category: {}", category);
        return courseRepository.findByCategory(category);
    }

    /**
     * Get courses by level
     */
    public List<Course> getCoursesByLevel(CourseLevel level) {
        log.debug("Fetching courses with level: {}", level);
        return courseRepository.findByLevel(level);
    }

    /**
     * Get published courses only
     */
    public List<Course> getPublishedCourses() {
        log.debug("Fetching published courses");
        return courseRepository.findByIsPublishedTrue();
    }

    /**
     * Search courses by title
     */
    public List<Course> searchCoursesByTitle(String title) {
        log.debug("Searching courses with title containing: {}", title);
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Update course
     * Only the course owner instructor can update
     */
    public Course updateCourse(String id, Course updatedCourse, String instructorEmail) {
        log.info("Updating course: {}", id);

        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        if (!course.getInstructorEmail().equals(instructorEmail)) {
            log.error("Instructor {} not authorized to update course {}", instructorEmail, id);
            throw new UnauthorizedCourseAccessException(id, instructorEmail);
        }

        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setCategory(updatedCourse.getCategory());
        course.setLevel(updatedCourse.getLevel());
        course.setPrice(updatedCourse.getPrice());
        course.setLanguage(updatedCourse.getLanguage());
        course.setThumbnailUrl(updatedCourse.getThumbnailUrl());
        course.setTags(updatedCourse.getTags());
        course.setModules(updatedCourse.getModules());
        course.setIsPublished(updatedCourse.getIsPublished());

        Course saved = courseRepository.save(course);
        log.info("Course updated successfully: {}", id);

        return saved;
    }

    /**
     * Delete course
     * Only instructor can
     */
    public void deleteCourse(String id, String instructorEmail) {
        log.info("Deleting course: {}", id);

        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        if (!course.getInstructorEmail().equals(instructorEmail)) {
            log.error("Instructor {} not authorized to delete course {}", instructorEmail, id);
            throw new UnauthorizedCourseAccessException(id, instructorEmail);
        }

        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", id);
    }

    /**
     * Toggle publish status
     */
    public Course togglePublishStatus(String id, String instructorEmail) {
        log.info("Toggling publish status for course: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        // Check authorization
        if (!course.getInstructorEmail().equals(instructorEmail)) {
            throw new UnauthorizedCourseAccessException(id, instructorEmail);
        }

        course.setIsPublished(!course.getIsPublished());
        Course updated = courseRepository.save(course);

        log.info("Course publish status toggled to: {}", updated.getIsPublished());
        return updated;
    }

    /**
     * Check if course exists
     */
    public boolean existsById(String id) {
        return courseRepository.existsById(id);
    }

    /**
     * Get total course count
     */
    public long getTotalCourseCount() {
        return courseRepository.count();
    }

    /**
     * Increment enrollment count
     * Called when student enrolls in course
     */
    public void incrementEnrollmentCount(String courseId) {
        log.info("Incrementing enrollment count for course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        Integer currentCount = course.getEnrollmentCount();
        course.setEnrollmentCount(currentCount == null ? 1 : currentCount + 1);

        courseRepository.save(course);
        log.info("Enrollment count for course {} incremented to: {}", courseId, course.getEnrollmentCount());
    }

    /**
     * Decrement enrollment count
     * Called when student drops enrollment
     */
    public void decrementEnrollmentCount(String courseId) {
        log.info("Decrementing enrollment count for course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        Integer currentCount = course.getEnrollmentCount();
        if (currentCount != null && currentCount > 0) {
            course.setEnrollmentCount(currentCount - 1);
        } else {
            log.warn("Enrollment count for course {} is already 0 or null", courseId);
            course.setEnrollmentCount(0);
        }

        courseRepository.save(course);
        log.info("Enrollment count for course {} decremented to: {}", courseId, course.getEnrollmentCount());
    }

    /**
     * Get total lesson count across all modules
     * Called by Progress Service to calculate completion percentage
     */
    public int getTotalLessonCount(String courseId) {
        log.info("Getting total lesson count for course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        int totalLessons = course.getModules().stream()
                .mapToInt(module -> module.getLessons() != null ? module.getLessons().size() : 0)
                .sum();

        log.info("Course {} has {} total lessons", courseId, totalLessons);
        return totalLessons;
    }

    /**
     * Get specific lesson details by module and lesson index
     * Called by Progress Service to denormalize lesson title and duration
     */
    public com.elearning.course.dto.LessonDetailsDTO getLessonDetails(String courseId, int moduleIndex,
            int lessonIndex) {
        log.info("Getting lesson details for course: {}, module: {}, lesson: {}",
                courseId, moduleIndex, lessonIndex);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (course.getModules() == null || moduleIndex >= course.getModules().size()) {
            throw new IllegalArgumentException("Module index " + moduleIndex + " not found in course");
        }

        var module = course.getModules().get(moduleIndex);

        if (module.getLessons() == null || lessonIndex >= module.getLessons().size()) {
            throw new IllegalArgumentException("Lesson index " + lessonIndex + " not found in module " + moduleIndex);
        }

        var lesson = module.getLessons().get(lessonIndex);

        return com.elearning.course.dto.LessonDetailsDTO.builder()
                .title(lesson.getTitle())
                .duration(lesson.getDuration())
                .build();
    }

}
