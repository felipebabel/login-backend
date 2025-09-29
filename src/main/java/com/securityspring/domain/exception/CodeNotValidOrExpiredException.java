package com.securityspring.domain.exception;

public class CodeNotValidOrExpiredException extends RuntimeException {
    public CodeNotValidOrExpiredException(String message) {
        super(message);
    }
}
