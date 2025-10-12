package com.elearning.user.model;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity - Represents a user in the e-learning platform
 * 
 * This class maps to the "users" table in POSTGRESQL database.
 */

 @Entity
 @Table(name = "users")
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder

public class User {

    /**
     * Primary key - Unique identifier for each user
     * 
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email - Unique email address for the user
     * 
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;


    /** 
     * Password - Stored encrypted (BCrypt hashing)
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Role - Defines access level (STUDENT, INSTRUCTOR, ADMIN)
     * 
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Enabled - Is the user account active?
     * 
     * true = active, false = disabled
     * 
     * Default is true
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Created At - Timestamp when the user was created
     * 
     * updatable = false - Cant change creatin time
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * UPDATED AT - Last time the account was modified
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    
}
