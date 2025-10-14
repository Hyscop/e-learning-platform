package com.elearning.progress.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseServiceClient {

    private final RestTemplate restTemplate;

    @Value("${course.service.url}")
    private String courseServiceUrl;

    public int getTotalLessonCount(String courseId) {
        String url = courseServiceUrl + "/api/courses/" + courseId + "/lesson-count";

        try {
            Integer count = restTemplate.getForObject(url, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Failed to get lesson count for course '{}': {}", courseId, e.getMessage());
            return 0;
        }
    }

    public LessonDetails getLessonDetails(String courseId, int moduleIndex, int lessonIndex) {
        String url = String.format("%s/api/courses/%s/modules/%d/lessons/%d",
                courseServiceUrl, courseId, moduleIndex, lessonIndex);

        try {
            return restTemplate.getForObject(url, LessonDetails.class);
        } catch (Exception e) {
            log.error("Failed to get lesson details: {}", e.getMessage());
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonDetails {
        private String title;
        private Integer duration;
    }
}