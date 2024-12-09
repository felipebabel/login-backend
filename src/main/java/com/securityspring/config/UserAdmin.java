package com.securityspring.config;

import com.securityspring.repository.UserRepository;
import com.securityspring.service.PasswordService;
import com.securityspring.util.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserAdmin implements CommandLineRunner {

    public static final String ADMIN = "admin";
    private final UserRepository userRepository;
    static final Logger LOGGER = LoggerFactory.getLogger("UserAdmin");


    private final PasswordService passwordService;
    public UserAdmin(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Creating user admin.");
            User adminUser = new User();
            adminUser.setUsername(ADMIN);
            adminUser.setPassword(this.passwordService.encryptPassword(ADMIN, ADMIN));
            userRepository.save(adminUser);
            LOGGER.info("User admin created.");
        }
}
