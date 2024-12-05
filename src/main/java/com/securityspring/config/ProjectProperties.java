package com.securityspring.config;


import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProjectProperties {

    private final Environment environment;

    public ProjectProperties(Environment environment) {
        this.environment = environment;
    }

    public String getProperty(final String key) {
        return this.environment.getProperty(key);
    }
}
