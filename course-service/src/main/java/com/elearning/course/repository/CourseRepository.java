package com.elearning.course.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.elearning.course.model.Course;
import com.elearning.course.model.CourseLevel;

/**
 * Course Repository
 */
public interface CourseRepository extends MongoRepository<Course, String> {

    /**
     * Find all courses by instructor email
     * 
     * Query: db.courses.find({ instructorEmail: email })
     * 
     * @param instructorEmail
     * @return list of courses by instructor
     */
    List<Course> findByInstructorEmail(String instructorEmail);

    /**
     * Find all courses with level
     * 
     * @param level
     * @return list of courses
     */
    List<Course> findByLevel(CourseLevel level);

    /**
     * Find all courses by category
     * 
     * @param category
     * @return List of courses
     */
    List<Course> findByCategory(String category);

    /**
     * Find all published courses
     * 
     * @return List of published courses
     */
    List<Course> findByIsPublishedTrue();

    /**
     * Find courses by category and level
     * 
     * @param category
     * @param level
     * @retuen list of courses
     */
    List<Course> findByCategoryAndLevel(String category, CourseLevel level);

    /**
     * Find courses containing specifi tag
     * 
     * @param tag
     * @return List of Courses
     */
    List<Course> findByTagsContaining(String tag);

    /**
     * Search courses by title
     * 
     * @param title
     * @return List of courses
     */
    List<Course> findByTitleContainingIgnoreCase(String title);
}
