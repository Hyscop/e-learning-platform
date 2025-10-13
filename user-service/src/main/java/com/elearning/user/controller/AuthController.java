package com.elearning.user.controller;

import com.elearning.user.dto.AuthRequest;
import com.elearning.user.dto.AuthResponse;
import com.elearning.user.model.User;
import com.elearning.user.security.JwtService;
import com.elearning.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Handles user login and JWT token generation
 * 
 * Endpoints:
 * - POST /api/users/auth/login - User login
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * LOGIN
     * 
     * POST /api/users/auth/login
     * 
     * Authenticates user and returns JWT token
     * 
     * @param authRequest Login credentials (email and password)
     * @return JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login attempt for email: {}", authRequest.getEmail());

        try {
            // Authenticate user with Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            log.info("Authentication successful for: {}", authRequest.getEmail());

            // Get user details
            User user = userService.getUserByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate JWT token
            String token = jwtService.generateToken(user.getEmail());

            // Build response
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .build();

            log.info("JWT token generated for user: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
