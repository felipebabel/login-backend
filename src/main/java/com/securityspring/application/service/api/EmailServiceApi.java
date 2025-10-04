package com.securityspring.application.service.api;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.domain.model.UserEntity;

public interface EmailServiceApi {

    void sendEmail(final String to,
                   final HttpServletRequest httpServletRequest) throws MessagingException;

    UserEntity validateCode(final String code,
                      final String email,
                            final HttpServletRequest httpServletRequest);


    void sendEmail(final UserEntity userEntity,
                   final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException;
}
