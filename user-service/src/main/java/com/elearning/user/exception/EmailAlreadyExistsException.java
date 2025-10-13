package com.elearning.user.exception;

/**
 * EMAIL ALREADY EXISTS EXCEPTION
 * 
 * 
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructor with email
     * 
     * @param email 
     */
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
