package com.securityspring.application.service.impl;

import com.securityspring.application.service.api.PasswordServiceApi;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class PasswordServiceImpl implements PasswordServiceApi {

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encryptPassword(final String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean checkPassword(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
