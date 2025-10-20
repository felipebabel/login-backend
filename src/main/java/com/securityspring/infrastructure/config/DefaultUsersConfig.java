package com.securityspring.infrastructure.config;

import javax.sql.DataSource;
import com.securityspring.application.service.api.LoginServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DefaultUsersConfig {

    @Autowired
    private LoginServiceApi loginService;

    private final ProjectProperties projectProperties;

    private final DataSource dataSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUsersConfig.class);

    public DefaultUsersConfig(ProjectProperties projectProperties, DataSource dataSource) {
        this.projectProperties = projectProperties;
        this.dataSource = dataSource;
    }

    @Bean
    public CommandLineRunner createAdminUser() {
        final boolean simulateData = Boolean.parseBoolean(this.projectProperties.getProperty("simulate-data"));
        final boolean createDefaultUsers = Boolean.parseBoolean(this.projectProperties.getProperty("create-default-users"));

        return args -> {
            if (createDefaultUsers) {
                this.loginService.createDefaultUsers();
                LOGGER.info("Default users created.");
            } else {
                LOGGER.info("Creation of default users skipped.");
            }
            if (simulateData) {
                final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db-init/2createLoginUser.sql"));
                populator.addScript(new ClassPathResource("db-init/3createLoginLogs.sql"));
                populator.execute(dataSource);
                LOGGER.info("Fake data created.");
            } else {
                LOGGER.info("Creation of fake data skipped.");
            }
        };
    }
}