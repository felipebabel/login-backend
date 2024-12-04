package com.securityspring.config;


import org.apache.catalina.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
