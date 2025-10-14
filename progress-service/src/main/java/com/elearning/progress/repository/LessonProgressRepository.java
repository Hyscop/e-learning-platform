package com.elearning.progress.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.elearning.progress.model.CompletionStatus;
import com.elearning.progress.model.LessonProgress;

@Repository
public interface LessonProgressRepository extends MongoRepository<LessonProgress, String> {

    List<LessonProgress> findByEnrollmentIdOrderByModuleIndexAscLessonIndexAsc(String enrollmentId);

    Optional<LessonProgress> findByEnrollmentIdAndModuleIndexAndLessonIndex(String enrollmentId, Integer moduleIndex,
            Integer lessonIndex);

    List<LessonProgress> findByStudentEmailOrderByCreatedAtDesc(String studentEmail);

    List<LessonProgress> findByCourseIdAndStudentEmailOrderByModuleIndexAscLessonIndexAsc(String courseId,
            String studentEmail);

    long countByCourseIdAndStudentEmailAndStatus(String courseId, String studentEmail, CompletionStatus status);

    void deleteByEnrollmentId(String enrollmentId);
}
