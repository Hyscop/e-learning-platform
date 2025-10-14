package com.elearning.enrollment.exception;

public class InvalidProgressException extends RuntimeException {
    public InvalidProgressException(String message) {
        super(message);
    }

    public InvalidProgressException(Integer progress) {
        super(String.format("Invalid progress value: %d. Progress must be between 0 and 100", progress));
    }
}
