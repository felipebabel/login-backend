package com.securityspring.service;

import com.securityspring.config.ProjectProperties;
import com.securityspring.controller.EmailApi;
import com.securityspring.repository.UserRepository;
import com.securityspring.util.Aes256Util;
import com.securityspring.util.DefaultResponse;
import com.securityspring.util.EmailDto;
import com.securityspring.util.User;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Scope("prototype")
public class LoginService {

    @Getter
    private final ProjectProperties projectProperties;

    private final EmailApi emailApi;

    private final UserRepository userRepository;
    static final Logger LOGGER = LoggerFactory.getLogger("LoginService");


    public LoginService(ProjectProperties projectProperties, EmailApi emailApi, UserRepository userRepository) {
        this.projectProperties = projectProperties;
        this.emailApi = emailApi;
        this.userRepository = userRepository;
    }

    public void saveUser(final String password, final String user, final String email, final String firstName)  {
        User userEntity = new User();
        userEntity.setUsername(user);
        userEntity.setPassword(password);
        userEntity.setFirstName(firstName);
        userEntity.setEmail(email);
        this.userRepository.save(userEntity);
        LOGGER.info("User saved: Id: {}",userEntity.getIdentifier());
    }

    public Optional<User> findUser(final String user)  {
        return this.userRepository.findUser(user);
    }

    public ResponseEntity<DefaultResponse> requestPasswordReset(final String name, final String email) {
        EmailDto emailDto = buildEmailDto(name, "123456", email);
        this.emailApi.sendEmail(emailDto);
        LOGGER.info("Password reset code sent to email.");
        return ResponseEntity.ok(DefaultResponse.builder()
                .message("Password reset code sent to email.")
                .build());
    };

    private EmailDto buildEmailDto(final String name, final String resetCode, final String email) {
        String body = String.format("Hello %s,\n\n" +
                        "We received a request to reset your password for your account on Login. Please use the following one-time code to reset your password.\n\n" +
                        "Your password reset code is: %s\n\n" +
                        "This code will expire in 10 minutes, so please use it soon. If you did not request a password reset, you can ignore this email. Your password will remain unchanged.\n\n" +
                        "To reset your password, please follow these steps:\n" +
                        "1. Enter the code above when prompted.\n" +
                        "2. Choose a new, secure password.\n\n" +
                        "Best regards,\n" +
                        "This email was sent to you because we received a request to reset your account's password. If you did not request this, please disregard this message.",
                name, resetCode);
        return EmailDto.builder().to(email)
                .body(body)
                .from(this.getEmailFrom()).build();
    }

    public void updatePassword(final String password, final User user)  {
        user.setPassword(password);
        this.userRepository.save(user);
    }

    public String decryptPassword(final String password, final String user) throws BadRequestException {
        return Aes256Util.decrypt(password, this.getSecretKey(), user);
    }

    private String getSecretKey() {
        return this.projectProperties.getProperty("security-project.secret-key");
    }

    public String getEmailFrom() {
        return this.projectProperties.getProperty("email-service.from");
    }
}
