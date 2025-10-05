package com.securityspring.application.service.api;

import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;

public interface ConfigServiceApi {

    ConfigVO setConfig(final String key,
                       final String value,
                       final Long userRequired,
                       final HttpServletRequest httpServletRequest);

    ConfigVO getConfig(final String key) throws ConfigNotFoundException;

}
