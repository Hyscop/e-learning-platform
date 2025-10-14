package com.elearning.progress.dto;

import java.time.format.DateTimeFormatter;

import com.elearning.progress.model.CompletionStatus;
import com.elearning.progress.model.LessonProgress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressResponse {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String id;
    private String enrollmentId;
    private String courseId;
    private String courseTitle;
    private Integer moduleIndex;
    private Integer lessonIndex;
    private String lessonTitle;
    private CompletionStatus status;
    private Integer videoWatchedSeconds;
    private Integer totalDurationSeconds;
    private String completedAt; // Changed to String for easier JSON serialization

    public static LessonProgressResponse fromEntity(LessonProgress progress) {
        return LessonProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollmentId())
                .courseId(progress.getCourseId())
                .courseTitle(progress.getCourseTitle())
                .moduleIndex(progress.getModuleIndex())
                .lessonIndex(progress.getLessonIndex())
                .lessonTitle(progress.getLessonTitle())
                .status(progress.getStatus())
                .videoWatchedSeconds(progress.getVideoWatchedSeconds())
                .totalDurationSeconds(progress.getTotalDurationSeconds())
                .completedAt(progress.getCompletedAt() != null ? progress.getCompletedAt().format(FORMATTER) : null)
                .build();
    }

}
