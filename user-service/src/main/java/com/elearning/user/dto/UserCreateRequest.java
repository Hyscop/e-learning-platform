package com.elearning.user.dto;

import com.elearning.user.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User create request DTO for admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    /**
     * Email - must be valid email
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Password 
     */
    @NotBlank(message = "Password is also required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

     /**
     * First Name
     */
    @NotBlank(message = "Required again")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    /**
     * Last Name
     */
    @NotBlank(message = "You know all these required right?")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    /**
     * Role - Admin can specify role
     */
    @NotNull(message = "ROLE IS REQUIRED")
    private Role role;
}
