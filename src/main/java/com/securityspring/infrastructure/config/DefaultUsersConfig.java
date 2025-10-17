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
        final boolean initDatabase = Boolean.parseBoolean(this.projectProperties.getProperty("init-database"));
        return args -> {
            if (initDatabase) {
                this.loginService.createDefaultUsers();
                final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db-init/login_user.sql"));
                populator.addScript(new ClassPathResource("db-init/login_logs.sql"));
                populator.execute(dataSource);
                LOGGER.info("Default users and data created.");
            } else {
                LOGGER.info("Creation of default users and data skipped.");
            }
        };
    }
}