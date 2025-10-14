package com.elearning.enrollment.client;

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

    @Value("${course.service.url:http://localhost:8082}")
    private String courseServiceUrl;

    public String getCourseTitle(String courseId) {
        try {
            log.debug("Fetching course title for courseId: {}", courseId);

            String url = courseServiceUrl + "/api/courses/details/" + courseId;
            CourseDetailsResponse response = restTemplate.getForObject(url, CourseDetailsResponse.class);

            if (response != null && response.getTitle() != null) {
                log.debug("Course title fetched: {}", response.getTitle());
                return response.getTitle();
            }

            log.warn("Course title not found for courseId: {}", courseId);
            return "Unknown Course";

        } catch (Exception e) {
            log.error("Error fetching course title for courseId: {}", courseId, e);
            return "Unknown Course";
        }
    }

    public void incrementEnrollmentCount(String courseId) {
        try {
            log.debug("Incrementing enrollment count for courseId: {}", courseId);

            String url = courseServiceUrl + "/api/courses/" + courseId + "/enrollment/increment";
            restTemplate.postForEntity(url, null, Void.class);

            log.debug("Successfully incremented enrollment count for courseId: {}", courseId);
        } catch (Exception e) {
            log.error("Error incrementing enrollment count for courseId: {}", courseId, e);
            throw new RuntimeException("Failed to increment enrollment count for course: " + courseId, e);
        }
    }

    public void decrementEnrollmentCount(String courseId) {
        try {
            log.debug("Decrementing enrollment count for courseId: {}", courseId);

            String url = courseServiceUrl + "/api/courses/" + courseId + "/enrollment/decrement";
            restTemplate.postForEntity(url, null, Void.class);

            log.debug("Successfully decremented enrollment count for courseId: {}", courseId);
        } catch (Exception e) {
            log.error("Error decrementing enrollment count for courseId: {}", courseId, e);
            throw new RuntimeException("Failed to decrement enrollment count for course: " + courseId, e);
        }
    }

    @lombok.Data
    private static class CourseDetailsResponse {
        private String id;
        private String title;
        private String description;
        private String instructorEmail;
        private String instructorFirstName;
        private String instructorLastName;
    }
}
