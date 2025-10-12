package com.securityspring.infrastructure.config;

import com.securityspring.application.service.api.LoginServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultUsersConfig {

    @Autowired
    private LoginServiceApi loginService;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            this.loginService.createDefaultUsers();
        };
    }
}