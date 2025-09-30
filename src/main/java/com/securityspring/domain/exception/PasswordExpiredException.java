package com.securityspring.domain.exception;

public class PasswordExpiredException extends RuntimeException {
    public PasswordExpiredException(String message) {
        super(message);
    }
}