package com.elearning.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error Response DTO
 * 
 * Standard error response format for all API errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp when error occured
     */
    private LocalDateTime timestamp;

    /**
     * Http status code (404, 400, 500, etc.)
     */
    private int status;

    /**
     * Http status name (Not Found, Bad Request, etc.)
     */
    private String error;

    /**
     * Detailed message
     */
    private String message;

    /**
     * Requset path that caused error
     */

     private String path;



    
}
