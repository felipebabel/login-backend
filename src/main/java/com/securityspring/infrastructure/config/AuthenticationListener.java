package com.securityspring.infrastructure.config;

import com.securityspring.application.service.api.LoginServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {

    private final LoginServiceApi loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationListener.class);

    public AuthenticationListener(LoginServiceApi loginService) {
        this.loginService = loginService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() != null && event.getAuthentication().isAuthenticated()) {
            String username = event.getAuthentication().getName();

            try {
                this.loginService.updateLastAccess(username);
                LOGGER.info("Last access updated for user: {}", username);
            } catch (Exception e) {
                LOGGER.error("Failed to update last access for user {}: {}", username, e.getMessage());
            }
        }
    }
}
