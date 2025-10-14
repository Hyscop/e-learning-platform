package com.elearning.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * JWT Authentication Filter for API Gateway
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of(

            // ==================== USER SERVICE ====================
            // Public endpoints
            "/api/users/register",
            "/api/users/auth/login",
            "/api/users/auth/logout",

            // ==================== COURSE SERVICE ====================

            "/api/courses/details/",
            "/api/courses/published",
            "/api/courses/category/",
            "/api/courses/level/",
            "/api/courses/search",
            "/api/courses/instructor/",
            "/api/courses/count",
            "/api/courses/exists/",

            // ==================== ENROLLMENT SERVICE ====================
            "/api/enrollments/course/",

            // ==================== SYSTEM ====================
            "/actuator");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();

        log.info("Processing request: {} {}", method, path);

        if (path.equals("/api/courses") && "GET".equals(method)) {
            log.info("Public endpoint accessed: GET {}", path);
            return chain.filter(exchange);
        }

        // Skip JWT validation for other public endpoints
        if (isPublicEndpoint(path)) {
            log.info("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        // Allow service-to-service calls for lesson metadata (Progress Service calling
        // Course Service)
        // Pattern: /api/courses/{id}/lesson-count or
        // /api/courses/{id}/modules/{moduleIndex}/lessons/{lessonIndex}
        if (path.matches("/api/courses/[a-f0-9]{24}/lesson-count") ||
                path.matches("/api/courses/[a-f0-9]{24}/modules/\\d+/lessons/\\d+")) {
            log.info("Service-to-service endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        // Allow service-to-service calls for enrollment details (Progress Service
        // calling Enrollment Service)
        // Pattern: /api/enrollments/{mongodbId} - only match MongoDB ObjectId format
        // (24 hex characters)
        if (path.matches("/api/enrollments/[a-f0-9]{24}") && "GET".equals(method)) {
            log.info("Service-to-service endpoint accessed: GET {}", path);
            return chain.filter(exchange);
        }

        // Check if Authorization header exists
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.error("Missing Authorization header for: {} {}", method, path);
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Validate Bearer token format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Invalid Authorization header format");
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // REACTIVE JWT VALIDATION

        return jwtUtil.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.error("Invalid or expired JWT token");
                        return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                    }

                    // Extract username reactively
                    return jwtUtil.extractUsername(token)
                            .flatMap(username -> jwtUtil.extractRole(token)
                                    .flatMap(role -> jwtUtil.extractFirstName(token)
                                            .flatMap(firstName -> jwtUtil.extractLastName(token)
                                                    .flatMap(lastName -> {
                                                        log.info(
                                                                "JWT token validated successfully for user: {} ({} {}) with role: {}",
                                                                username, firstName, lastName, role);

                                                        // Add user information to request headers for downstream
                                                        // services
                                                        ServerHttpRequest modifiedRequest = exchange.getRequest()
                                                                .mutate()
                                                                .header("X-User-Email", username)
                                                                .header("X-User-Role", role)
                                                                .header("X-User-FirstName", firstName)
                                                                .header("X-User-LastName", lastName)
                                                                .build();

                                                        return chain.filter(
                                                                exchange.mutate().request(modifiedRequest).build());
                                                    }))));
                })
                .onErrorResume(e -> {
                    // Catch any errors and return 401
                    log.error("JWT validation error: {}", e.getMessage());
                    return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
                });
    }

    /**
     * Check if the request path is a public endpoint
     * Uses exact match OR startsWith for paths ending with '/'
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_URLS.stream()
                .anyMatch(publicUrl -> {
                    // If public URL ends with '/', it's a prefix match (e.g.,
                    // "/api/courses/details/")
                    if (publicUrl.endsWith("/")) {
                        return path.startsWith(publicUrl);
                    }
                    // Otherwise, exact match only (e.g., "/api/courses/published")
                    // BUT also allow with trailing slash
                    return path.equals(publicUrl) || path.equals(publicUrl + "/");
                });
    }

    /**
     * Handle authentication errors
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorResponse = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                java.time.LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getURI().getPath());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // High priority - run before other filters
    }
}
