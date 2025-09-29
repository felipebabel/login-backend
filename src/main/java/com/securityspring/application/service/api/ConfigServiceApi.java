package com.securityspring.application.service.api;

import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.domain.model.ConfigEntity;
import org.apache.coyote.BadRequestException;

public interface ConfigServiceApi {

    ConfigEntity setConfig(final String key,
                           final String value,
                           final Long userRequired);

    ConfigEntity getConfig(final String key) throws ConfigNotFoundException;

}
