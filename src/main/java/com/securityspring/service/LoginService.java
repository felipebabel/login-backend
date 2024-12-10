package com.securityspring.service;

import com.securityspring.config.ProjectProperties;
import com.securityspring.repository.UserRepository;
import com.securityspring.util.Aes256Util;
import com.securityspring.util.User;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Scope("prototype")
public class LoginService {

    @Getter
    private final ProjectProperties projectProperties;

    private final UserRepository userRepository;
    static final Logger LOGGER = LoggerFactory.getLogger("LoginService");


    public LoginService(ProjectProperties projectProperties, UserRepository userRepository) {
        this.projectProperties = projectProperties;
        this.userRepository = userRepository;
    }

    public void saveUser(final String password, final String user)  {
        User userEntity = new User();
        userEntity.setUsername(user);
        userEntity.setPassword(password);
        this.userRepository.save(userEntity);
        LOGGER.info("User saved: Id: {}",userEntity.getIdentifier());
    }

    public Optional<User> findUser(final String user)  {
        return this.userRepository.findUser(user);
    }

    public String decryptPassword(final String password, final String user) throws BadRequestException {
        return Aes256Util.decrypt(password, this.getSecretKey(), user);
    }

    private String getSecretKey() {
        return this.projectProperties.getProperty("security-project.secret-key");
    }
}
