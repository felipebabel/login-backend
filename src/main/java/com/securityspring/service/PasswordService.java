package com.securityspring.service;

import com.securityspring.config.ProjectProperties;
import com.securityspring.util.Aes256Util;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class PasswordService {

    @Getter
    private final ProjectProperties projectProperties;

    public PasswordService(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    public String encryptPassword(final String password) throws BadRequestException {
        return Aes256Util.encrypt(password, this.getSecretKey(), String.valueOf(1L));
    }

    public String decryptPassword(final String password) throws Exception {
        return Aes256Util.decrypt(password, this.getSecretKey(), String.valueOf(1L));
    }

    private String getSecretKey() {
        return this.projectProperties.getProperty("security-project.secret-key");
    }
}
