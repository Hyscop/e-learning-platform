package com.elearning.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT Utility Service for API Gateway (REACTIVE VERSION)
 * 
 * Validates JWT tokens and extracts user information
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Extract username (email) from JWT token
     */
    public Mono<String> extractUsername(String token) {
        return Mono.fromCallable(() -> extractClaim(token, Claims::getSubject));
    }

    /**
     * Extract role from JWT token
     */
    public Mono<String> extractRole(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        });
    }

    /**
     * Extract first name from JWT token
     */
    public Mono<String> extractFirstName(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = extractAllClaims(token);
            return claims.get("firstName", String.class);
        });
    }

    /**
     * Extract last name from JWT token
     */
    public Mono<String> extractLastName(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = extractAllClaims(token);
            return claims.get("lastName", String.class);
        });
    }

    /**
     * Extract expiration date from JWT token
     */
    public Mono<Date> extractExpiration(String token) {
        return Mono.fromCallable(() -> extractClaim(token, Claims::getExpiration));
    }

    /**
     * Extract specific claim from JWT token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Validate JWT token
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                return !isTokenExpired(token);
            } catch (Exception e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Get signing key from secret
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
