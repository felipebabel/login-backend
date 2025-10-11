package com.securityspring.infrastructure.adapters.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.ConfigServiceApi;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.infrastructure.adapters.api.ConfigApi;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@RestController
@RequestMapping("/api/v1/config")
public class ConfigController implements ConfigApi {

    private final ConfigServiceApi configService;

    static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    public ConfigController(final ConfigServiceApi configService) {
        this.configService = configService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("set-config")
    public ResponseEntity<Object> setConfig(@RequestParam("configValue") final Long configValue,
                                                            @RequestParam("configDescription") final String configDescription,
                                                            @RequestParam("userRequired") final Long userRequired,
                                                            final HttpServletRequest httpServletRequest) {
        final ConfigVO config = this.configService.setConfig(configDescription, configValue.toString(),
                userRequired, httpServletRequest);
        return new ResponseEntity<>(config, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @GetMapping("get-config")
    public ResponseEntity<Object> getConfig(@RequestParam("configDescription") final String configDescription) throws ConfigNotFoundException {
        ConfigVO config = this.configService.getConfig(configDescription);
        LOGGER.info("Get password expiry successfully.");
        return new ResponseEntity<>(config, HttpStatus.OK);
    }


}