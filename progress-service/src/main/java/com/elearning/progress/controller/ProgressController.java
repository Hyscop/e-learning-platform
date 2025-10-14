package com.elearning.progress.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.progress.dto.CourseProgressSummary;
import com.elearning.progress.dto.LessonProgressResponse;
import com.elearning.progress.dto.UpdateVideoProgressRequest;
import com.elearning.progress.service.ProgressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/video/update")
    public ResponseEntity<LessonProgressResponse> updateVideoProgress(
            @Valid @RequestBody UpdateVideoProgressRequest request,
            @RequestHeader("X-User-Email") String studentEmail) {

        log.info("Updating video progress - Enrollment: {}, Watched: {} seconds",
                request.getEnrollmentId(), request.getWatchedSeconds());

        LessonProgressResponse response = progressService.updateVideoProgress(request, studentEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseProgressSummary> getCourseProgress(
            @PathVariable String courseId,
            @RequestHeader("X-User-Email") String studentEmail) {

        log.info("Fetching course progress - Course: {}, Student: {}", courseId, studentEmail);

        CourseProgressSummary summary = progressService.getCourseProgress(courseId, studentEmail);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/my-progress")
    public ResponseEntity<List<LessonProgressResponse>> getMyProgress(
            @RequestHeader("X-User-Email") String studentEmail) {

        log.info("Fetching all progress for student: {}", studentEmail);

        List<LessonProgressResponse> progress = progressService.getMyProgress(studentEmail);
        return ResponseEntity.ok(progress);
    }

}
