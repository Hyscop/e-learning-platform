package com.elearning.progress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressSummary {

    private String courseId;
    private String courseTitle;
    private String studentEmail;
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer inProgressLessons;
    private Double completionPercentage;
    private List<LessonProgressResponse> lessonProgress;
}