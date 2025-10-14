package com.elearning.progress.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${enrollment.service.url}")
    private String enrollmentServiceUrl;

    public void updateEnrollmentProgress(String enrollmentId, double progressPercentage) {
        String url = enrollmentServiceUrl + "/api/enrollments/" + enrollmentId + "/progress";

        Map<String, Object> request = new HashMap<>();
        request.put("progress", progressPercentage);

        try {
            restTemplate.put(url, request);
            log.info("Updated enrollment '{}' progress to {}%", enrollmentId, progressPercentage);
        } catch (Exception e) {
            log.error("Failed to update enrollment progress: {}", e.getMessage());
        }
    }

    public EnrollmentDetails getEnrollmentDetails(String enrollmentId) {
        String url = enrollmentServiceUrl + "/api/enrollments/" + enrollmentId;

        try {
            EnrollmentDetails details = restTemplate.getForObject(url, EnrollmentDetails.class);
            log.info("Retrieved enrollment details for: {}", enrollmentId);
            return details;
        } catch (Exception e) {
            log.error("Failed to get enrollment details: {}", e.getMessage());
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentDetails {
        private String id;
        private String studentEmail;
        private String courseId;
        private String courseTitle;
        private Double progressPercentage;
        private String status;
    }
}