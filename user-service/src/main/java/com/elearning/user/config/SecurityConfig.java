package com.elearning.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security config
 * 
 * Provides security-related beans (temporarily basic config)
 * Currently DISABLES authentication for testing
 * Will add JWT authentication later
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password encode bean
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    /**
     * SECURITY FILTER CHAIN - Temporarily disable authentication
     * 
     * WHY? So we can test API endpoints without login
     * 
     * IMPORTANT: This is for DEVELOPMENT ONLY!
     * We'll add proper JWT authentication later
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests without authentication
            );
        
        return http.build();
    }

}

