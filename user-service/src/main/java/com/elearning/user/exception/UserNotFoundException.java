package com.elearning.user.exception;


/**
 * User not found exception
 * 
 * Throw when a user is requested but doesnt exists in db
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor with custom message
     * 
     * @param message error message
     */
    public UserNotFoundException(String message){
        super(message);
    }

    /**
     * Constructor with ID
     * 
     * @param id 
     */
    public UserNotFoundException(Long id){
        super("User not found with ID: " + id);
    }

}
