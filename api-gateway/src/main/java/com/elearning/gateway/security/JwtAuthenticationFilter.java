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
import java.util.function.Predicate;

/**
 * JWT Authentication Filter for API Gateway
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_URLS = List.of(
            "/api/users/register",
            "/api/users/auth/login",
            "/api/users/auth/logout",
            "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("Processing request: {} {}", request.getMethod(), request.getURI().getPath());

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(request.getURI().getPath())) {
            log.info("Public endpoint accessed: {}", request.getURI().getPath());
            return chain.filter(exchange);
        }

        // Check if Authorization header exists
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.error("Missing Authorization header");
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
        // flatMap() = Mono içindeki değerle başka bir Mono işlemi yap
        return jwtUtil.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.error("Invalid or expired JWT token");
                        return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                    }

                    // Extract username reactively
                    return jwtUtil.extractUsername(token)
                            .flatMap(username -> {
                                log.info("JWT token validated successfully for user: {}", username);

                                // Add user information to request headers for downstream services
                                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                        .header("X-User-Email", username)
                                        .build();

                                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                            });
                })
                .onErrorResume(e -> {
                    // Catch any errors and return 401
                    log.error("JWT validation error: {}", e.getMessage());
                    return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
                });
    }

    /**
     * Check if the request path is a public endpoint
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_URLS.stream()
                .anyMatch(path::startsWith);
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
                exchange.getRequest().getURI().getPath()
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // High priority - run before other filters
    }
}
