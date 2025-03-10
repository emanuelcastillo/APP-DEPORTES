package com.deportes.api.exception;

public class CustomValidationException extends RuntimeException {
    public CustomValidationException(String message) {
        super(message);
    }
}
