package com.securityspring.application.service.impl;

import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.ConfigServiceApi;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.domain.port.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class ConfigServiceImpl implements ConfigServiceApi {

    private final ConfigRepository configRepository;

    private final LogServiceApi logService;


    static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);


    public ConfigServiceImpl(final ConfigRepository configRepository,
                             final LogServiceApi logService) {
        this.configRepository = configRepository;
        this.logService = logService;
    }

    @Override
    public ConfigEntity setConfig(final String key,
                                  final String value,
                                  final Long userRequired,
                                  final HttpServletRequest httpServletRequest) {
        final ConfigEntity configEntity = this.configRepository.findByKey(key).orElse(new ConfigEntity());

        configEntity.setKey(key);
        configEntity.setValue(value);
        configEntity.setUpdateDate(LocalDateTime.now());
        this.configRepository.save(configEntity);
        LOGGER.info("Setting key: {} with value: {}, user required: {}", key, value, userRequired);
        this.logService.setLog("CONFIG", String.format("Setting key: %s with value: %s, user required: %d",
                key, value, userRequired), httpServletRequest);
        return configEntity;
    }

    @Override
    public ConfigEntity getConfig(final String key) throws ConfigNotFoundException {
        return this.configRepository.findByKey(key)
                .orElseThrow(() -> new ConfigNotFoundException("Not found configuration for key: " + key));
    }
}
