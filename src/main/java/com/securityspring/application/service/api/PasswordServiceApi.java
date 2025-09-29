package com.securityspring.application.service.api;

public interface PasswordServiceApi {

    String encryptPassword(final String password);

    boolean checkPassword(final String rawPassword, final String encodedPassword);
}
