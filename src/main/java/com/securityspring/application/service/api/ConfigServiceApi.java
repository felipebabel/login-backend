package com.securityspring.application.service.api;

import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.domain.model.ConfigEntity;

public interface ConfigServiceApi {

    ConfigEntity setConfig(final String key,
                           final String value,
                           final Long userRequired,
                           final HttpServletRequest httpServletRequest);

    ConfigEntity getConfig(final String key) throws ConfigNotFoundException;

}
