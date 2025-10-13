package com.elearning.user.controller;

import com.elearning.user.dto.UserCreateRequest;
import com.elearning.user.dto.UserRegistrationRequest;
import com.elearning.user.dto.UserResponse;
import com.elearning.user.exception.UserNotFoundException;
import com.elearning.user.model.Role;
import com.elearning.user.model.User;
import com.elearning.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Controller - REST API endpoints for user management
 * Endpoints:
 * - POST /register - Public user registration
 * - POST /create - Create user (admin)
 * - GET / - Get all users
 * - GET /{id} - Get user by ID
 * - GET /email/{email} - Get user by email
 * - GET /role/{role} - Get users by role
 * - PUT /{id} - Update user
 * - DELETE /{id} - Delete user
 * - PATCH /{id}/disable - Disable user account
 * 
 * 
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // ========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ========================================

    /**
     * REGISTER NEW USER (Public endpoint)
     * 
     * POST /api/users/register
     * 
     * Request Body:
     * {
     * "email": "john@example.com",
     * "password": "password123",
     * "firstName": "John",
     * "lastName": "Doe"
     * }
     * 
     * Response: 201 Created + UserResponse DTO
     * 
     * @param request Registration data
     * @return Created user (without password)
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User registeredUser = userService.registerUser(user);

        UserResponse response = new UserResponse(registeredUser);

        log.info("User registered successfully with ID: {}", registeredUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========================================
    // ADMIN ENDPOINTS - Require ADMIN role
    // ========================================

    /**
     * CREATE USER (Admin only)
     * 
     * POST /api/users/create
     * 
     * Request Body:
     * {
     * "email": "instructor@example.com",
     * "password": "password123",
     * "firstName": "Jane",
     * "lastName": "Smith",
     * "role": "INSTRUCTOR"
     * }
     * 
     * Response: 201 Created + UserResponse DTO
     * 
     * @param request User creation data with role
     * @return Created user
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        log.info("Create user request for email: {} with role: {}",
                request.getEmail(), request.getRole());

        // Convert DTO to Entity
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .enabled(true)
                .build();

        // Create user
        User createdUser = userService.createUser(user);

        // Convert to DTO
        UserResponse response = new UserResponse(createdUser);

        log.info("User created successfully with ID: {}", createdUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET ALL USERS
     * 
     * GET /api/users
     * 
     * Response: 200 OK + List<UserResponse>
     * 
     * TODO: Add pagination (Page<UserResponse>)
     * 
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Get all users request received");

        List<User> users = userService.getAllUsers();

        // Convert List<User> to List<UserResponse>
        List<UserResponse> response = users.stream()
                .map(UserResponse::new) // Convert each User to UserResponse
                .collect(Collectors.toList());

        log.info("Returning {} users", response.size());

        return ResponseEntity.ok(response);
    }

    /**
     * GET USER BY ID
     * 
     * GET /api/users/{id}
     * 
     * Example: GET /api/users/1
     * 
     * Response: 200 OK + UserResponse
     * 404 Not Found if user doesn't exist
     * 
     * @param id User ID from URL path
     * @return User details
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request: {}", id);

        User user = userService.getUserById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });

        UserResponse response = new UserResponse(user);

        return ResponseEntity.ok(response);
    }

    /**
     * GET USER BY EMAIL
     * 
     * GET /api/users/email/{email}
     * 
     * Example: GET /api/users/email/john@example.com
     * 
     * @param email User email from URL path
     * @return User details
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Get user by email request: {}", email);

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        UserResponse response = new UserResponse(user);

        return ResponseEntity.ok(response);
    }

    /**
     * GET USERS BY ROLE
     * 
     * GET /api/users/role/{role}
     * 
     * Example: GET /api/users/role/STUDENT
     * 
     * @param role User role (STUDENT, INSTRUCTOR, ADMIN)
     * @return List of users with that role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        log.info("Get users by role request: {}", role);

        List<User> users = userService.getUsersByRole(role);

        List<UserResponse> response = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        log.info("Returning {} users with role {}", response.size(), role);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE USER
     * 
     * PUT /api/users/{id}
     * 
     * Request Body:
     * {
     * "firstName": "Updated",
     * "lastName": "Name",
     * "email": "newemail@example.com"
     * }
     * 
     * Response: 200 OK + Updated UserResponse
     * 
     * @param id      User ID to update
     * @param request Update data
     * @return Updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserCreateRequest request) {

        log.info("Update user request for ID: {}", id);

        // Convert DTO to Entity
        User updatedData = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        // Update user
        User updatedUser = userService.updateUser(id, updatedData);

        UserResponse response = new UserResponse(updatedUser);

        log.info("User updated successfully: {}", id);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE USER
     * 
     * DELETE /api/users/{id}
     * 
     * Response: 204 No Content
     * 
     * @param id User ID to delete
     * @return No content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.warn("Delete user request for ID: {}", id);

        userService.deleteUser(id);

        log.info("User deleted: {}", id);

        // 204 No Content - successful deletion
        return ResponseEntity.noContent().build();
    }

    /**
     * DISABLE USER ACCOUNT (Soft delete)
     * 
     * PATCH /api/users/{id}/disable
     * 
     * Response: 200 OK + Updated UserResponse
     * 
     * @param id User ID to disable
     * @return Disabled user
     */
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Long id) {
        log.info("Disable user request for ID: {}", id);

        User disabledUser = userService.disableUser(id);

        UserResponse response = new UserResponse(disabledUser);

        log.info("User disabled: {}", id);

        return ResponseEntity.ok(response);
    }

    // ========================================
    // UTILITY ENDPOINTS
    // ========================================

    /**
     * GET USER COUNT BY ROLE (Statistics)
     * 
     * GET /api/users/count/role/{role}
     * 
     * Example: GET /api/users/count/role/STUDENT
     * 
     * @param role Role to count
     * @return Number of users with that role
     */
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable Role role) {
        log.info("Count users by role request: {}", role);

        long count = userService.countUsersByRole(role);

        return ResponseEntity.ok(count);
    }

}
