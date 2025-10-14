package com.elearning.progress.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elearning.progress.client.CourseServiceClient;
import com.elearning.progress.client.EnrollmentServiceClient;
import com.elearning.progress.dto.CourseProgressSummary;
import com.elearning.progress.dto.LessonProgressResponse;
import com.elearning.progress.dto.UpdateVideoProgressRequest;
import com.elearning.progress.model.CompletionStatus;
import com.elearning.progress.model.LessonProgress;
import com.elearning.progress.repository.LessonProgressRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressService {

    private final LessonProgressRepository progressRepository;
    private final EnrollmentServiceClient enrollmentClient;
    private final CourseServiceClient courseClient;

    @CacheEvict(value = "lessonProgress", key = "#request.enrollmentId + '_' + #request.moduleIndex + '_' + #request.lessonIndex")
    public LessonProgressResponse updateVideoProgress(UpdateVideoProgressRequest request, String studentEmail) {
        log.info("Updating video progress - Enrollment: {}, Watched: {} seconds",
                request.getEnrollmentId(), request.getWatchedSeconds());

        LessonProgress progress = progressRepository
                .findByEnrollmentIdAndModuleIndexAndLessonIndex(
                        request.getEnrollmentId(),
                        request.getModuleIndex(),
                        request.getLessonIndex())
                .orElseGet(() -> createNewProgress(request, studentEmail));

        progress.setVideoWatchedSeconds(request.getWatchedSeconds());
        progress.setLastAccessedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        // AUTO-COMPLETE: Mark as completed when student watches 90%+ of video
        if (progress.getTotalDurationSeconds() != null &&
                request.getWatchedSeconds() >= progress.getTotalDurationSeconds() * 0.9) {

            if (progress.getStatus() != CompletionStatus.COMPLETED) {
                progress.setStatus(CompletionStatus.COMPLETED);
                progress.setCompletedAt(LocalDateTime.now());
                log.info("Lesson auto-completed - Student '{}' watched 90%+ of video (Module: {}, Lesson: {})",
                        studentEmail, request.getModuleIndex(), request.getLessonIndex());

                // Update enrollment progress when lesson is completed
                LessonProgress saved = progressRepository.save(progress);
                double overallProgress = calculateOverallProgress(request.getEnrollmentId(), saved.getCourseId());
                enrollmentClient.updateEnrollmentProgress(request.getEnrollmentId(), overallProgress);

                return LessonProgressResponse.fromEntity(saved);
            }
        } else if (progress.getStatus() == CompletionStatus.NOT_STARTED) {
            progress.setStatus(CompletionStatus.IN_PROGRESS);
            progress.setStartedAt(LocalDateTime.now());
        }

        return LessonProgressResponse.fromEntity(progressRepository.save(progress));
    }

    @Cacheable(value = "courseProgress", key = "#courseId + '_' + #studentEmail")
    public CourseProgressSummary getCourseProgress(String courseId, String studentEmail) {
        log.info("Getting course progress - Course: {}, Student: {}", courseId, studentEmail);

        List<LessonProgress> progressList = progressRepository
                .findByCourseIdAndStudentEmailOrderByModuleIndexAscLessonIndexAsc(courseId, studentEmail);

        long completedCount = progressRepository.countByCourseIdAndStudentEmailAndStatus(
                courseId, studentEmail, CompletionStatus.COMPLETED);
        long inProgressCount = progressRepository.countByCourseIdAndStudentEmailAndStatus(
                courseId, studentEmail, CompletionStatus.IN_PROGRESS);

        int totalLessons = courseClient.getTotalLessonCount(courseId);

        double percentage = totalLessons > 0 ? (completedCount * 100.0 / totalLessons) : 0.0;

        // Get course title from progress list or use courseId as fallback
        String courseTitle = "";
        if (!progressList.isEmpty()) {
            courseTitle = progressList.get(0).getCourseTitle();
            
            // Update enrollment progress when getting course progress summary
            String enrollmentId = progressList.get(0).getEnrollmentId();
            enrollmentClient.updateEnrollmentProgress(enrollmentId, percentage);
            log.info("Updated enrollment '{}' progress to {}%", enrollmentId, percentage);
        } else {
            log.warn("No progress records found for course '{}' and student '{}'. Course title will be empty.",
                    courseId, studentEmail);
        }

        return CourseProgressSummary.builder()
                .courseId(courseId)
                .courseTitle(courseTitle)
                .studentEmail(studentEmail)
                .totalLessons(totalLessons)
                .completedLessons((int) completedCount)
                .inProgressLessons((int) inProgressCount)
                .completionPercentage(Math.round(percentage * 100.0) / 100.0)
                .lessonProgress(progressList.stream().map(LessonProgressResponse::fromEntity).toList())
                .build();
    }

    @Cacheable(value = "studentProgress", key = "#studentEmail")
    public List<LessonProgressResponse> getMyProgress(String studentEmail) {
        log.info("Getting all progress for student: {}", studentEmail);
        return progressRepository.findByStudentEmailOrderByCreatedAtDesc(studentEmail)
                .stream()
                .map(LessonProgressResponse::fromEntity)
                .toList();
    }

    private LessonProgress createNewProgress(Object request, String studentEmail) {
        String enrollmentId;
        Integer moduleIndex;
        Integer lessonIndex;

        if (request instanceof UpdateVideoProgressRequest videoRequest) {
            enrollmentId = videoRequest.getEnrollmentId();
            moduleIndex = videoRequest.getModuleIndex();
            lessonIndex = videoRequest.getLessonIndex();
        } else {
            throw new IllegalArgumentException("Unsupported request type");
        }

        // Fetch enrollment details to get courseId and courseTitle
        EnrollmentServiceClient.EnrollmentDetails enrollment = enrollmentClient.getEnrollmentDetails(enrollmentId);
        if (enrollment == null) {
            log.error("Enrollment not found: {}", enrollmentId);
            throw new IllegalArgumentException("Enrollment not found: " + enrollmentId);
        }

        String courseId = enrollment.getCourseId();
        String courseTitle = enrollment.getCourseTitle();

        // Fetch lesson details from Course Service
        CourseServiceClient.LessonDetails lessonDetails = courseClient.getLessonDetails(
                courseId, moduleIndex, lessonIndex);

        String lessonTitle = lessonDetails != null ? lessonDetails.getTitle() : "Unknown Lesson";
        Integer totalDuration = lessonDetails != null ? lessonDetails.getDuration() : null;

        log.info("Creating new progress - Course: {}, Lesson: {}, Duration: {}s",
                courseTitle, lessonTitle, totalDuration);

        return LessonProgress.builder()
                .enrollmentId(enrollmentId)
                .studentEmail(studentEmail)
                .courseId(courseId)
                .courseTitle(courseTitle)
                .moduleIndex(moduleIndex)
                .lessonIndex(lessonIndex)
                .lessonTitle(lessonTitle)
                .totalDurationSeconds(totalDuration)
                .status(CompletionStatus.NOT_STARTED)
                .videoWatchedSeconds(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private double calculateOverallProgress(String enrollmentId, String courseId) {
        long completedCount = progressRepository
                .findByEnrollmentIdOrderByModuleIndexAscLessonIndexAsc(enrollmentId)
                .stream()
                .filter(p -> p.getStatus() == CompletionStatus.COMPLETED)
                .count();

        int totalLessons = courseClient.getTotalLessonCount(courseId);

        return totalLessons > 0 ? (completedCount * 100.0 / totalLessons) : 0.0;
    }

}
