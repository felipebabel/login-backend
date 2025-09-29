package com.securityspring.domain.exception;

public class UserPendingException extends RuntimeException {
    public UserPendingException(String message) {
        super(message);
    }
}
