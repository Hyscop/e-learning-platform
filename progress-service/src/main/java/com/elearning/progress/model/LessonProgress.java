package com.elearning.progress.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lesson_progress")
@CompoundIndex(def = "{'enrollmentId': 1, 'moduleIndex': 1, 'lessonIndex': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String enrollmentId;
    private String studentEmail;
    private String courseId;

    private Integer moduleIndex;
    private Integer lessonIndex;
    private String lessonTitle;

    private CompletionStatus status;
    private Integer videoWatchedSeconds;
    private Integer totalDurationSeconds;

    private LocalDateTime startedAt;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;

    private String courseTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
