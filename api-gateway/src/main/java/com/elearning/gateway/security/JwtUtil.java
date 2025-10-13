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
 * REACTIVE NEDEN? 
 * - API Gateway Spring WebFlux kullanır (reactive)
 * - Tüm metotlar Mono<> veya Flux<> dönmeli
 * - Thread'ler beklemez, çok verimli!
 * 
 * Validates JWT tokens and extracts user information
 * Uses same secret key as User Service for token validation
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Extract username (email) from JWT token
     * 
     * REACTIVE: Mono<String> döner
     * Mono.fromCallable() = Blocking kodu reactive'e çevirir
     */
    public Mono<String> extractUsername(String token) {
        return Mono.fromCallable(() -> extractClaim(token, Claims::getSubject));
    }

    /**
     * Extract expiration date from JWT token
     * 
     * REACTIVE: Mono<Date> döner
     */
    public Mono<Date> extractExpiration(String token) {
        return Mono.fromCallable(() -> extractClaim(token, Claims::getExpiration));
    }

    /**
     * Extract specific claim from JWT token
     * 
     * BLOCKING: Gerçek JWT işlemi (JJWT kütüphanesi blocking)
     * Bu yüzden Mono.fromCallable() ile sarmalıyoruz
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * 
     * BLOCKING: JJWT parser blocking çalışır
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
     * 
     * BLOCKING: İç metot, Mono'ya sarmalamaya gerek yok
     */
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Validate JWT token
     * 
     * REACTIVE: Mono<Boolean> döner
     * - Token geçerliyse: Mono.just(true)
     * - Token geçersizse: Mono.just(false)
     * - Hata olursa: Exception yakala, Mono.just(false) dön
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
     * 
     * BLOCKING: Secret key oluşturma işlemi
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
