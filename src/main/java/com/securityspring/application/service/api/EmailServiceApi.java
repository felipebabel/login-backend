package com.securityspring.application.service.api;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import com.securityspring.domain.model.UserEntity;

public interface EmailServiceApi {

    void sendEmail(final String to);

    UserEntity validateCode(final String code,
                      final String email);


    void sendEmail(final UserEntity userEntity) throws MessagingException, UnsupportedEncodingException;
}
