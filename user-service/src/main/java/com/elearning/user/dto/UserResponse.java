package com.elearning.user.dto;

import java.time.LocalDateTime;

import com.elearning.user.model.Role;
import com.elearning.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Response DTO - used for all responses
 * 
 * Required BY:
 * - GET /users/{id}
 * - GET /users
 * - POST /register
 * - POST /users
 * - PUT /users/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Cons - convert user entity to response DTO
     * 
     * @param user User entity
     */

     public UserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
        this.enabled = user.getEnabled();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
     }

}
