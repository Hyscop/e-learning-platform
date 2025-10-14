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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * 
 * Handles user authentication and logout
 * 
 * Endpoints:
 * - POST /api/users/auth/login - User login
 * - POST /api/users/auth/logout - User logout
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
                            authRequest.getPassword()));

            log.info("Authentication successful for: {}", authRequest.getEmail());

            // Get user details
            User user = userService.getUserByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate JWT token with user claims (role, firstName, lastName)
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole().toString());
            claims.put("firstName", user.getFirstName());
            claims.put("lastName", user.getLastName());
            String token = jwtService.generateToken(claims, user.getEmail());

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

    /**
     * LOGOUT
     * 
     * POST /api/users/auth/logout
     * 
     * Logs out user (client-side token removal)
     * 
     * Note: Since JWT is stateless, actual logout happens on client side
     * by removing the token. This endpoint is for logging purposes and
     * future token blacklisting implementation.
     * 
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("Logout request received");

        // In JWT stateless architecture, logout is handled client-side
        // The client should remove the token from storage

        // Future enhancement: Implement token blacklist using Redis
        // to prevent token reuse before expiration

        return ResponseEntity.ok("Logged out successfully. Please remove the token from client.");
    }
}
