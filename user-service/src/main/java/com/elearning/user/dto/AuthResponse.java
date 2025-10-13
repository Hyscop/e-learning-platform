package com.elearning.user.dto;

import com.elearning.user.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auth Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    
}
