package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.api.LoginApi;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import org.apache.catalina.User;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController implements LoginApi {

    private final LoginServiceApi loginService;

    private final EmailServiceApi emailService;

    static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController(final LoginServiceApi loginService,
                           final EmailServiceApi emailService) {
        this.loginService = loginService;
        this.emailService = emailService;
    }

    @Override
    @PostMapping
    public ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequestDto) {
        final UserEntity userEntity = loginService.login(loginRequestDto);
        return new ResponseEntity<>(userEntity, HttpStatus.OK);
    }

    @Override
    @PostMapping("/create-account")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody final CreateAccountRequestDto createAccount) throws BadRequestException, MessagingException, UnsupportedEncodingException {
        LOGGER.info("Creating account");
        this.loginService.createAccount(createAccount);
        return new ResponseEntity<>(DefaultResponse.builder().message("Account created successfully")
                .status(DefaultResponse.SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Override
    @PutMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("user") final Long userIdentifier) {
        LOGGER.info("User logging out");
        this.loginService.logout(userIdentifier);
        LOGGER.info("User logged out");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") final String email) {
        LOGGER.info("Sending email to {}", email);
        this.emailService.sendEmail(email);
        LOGGER.info("Email sent to {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/validate-code")
    public ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                               @RequestParam("email") final String email) {
        LOGGER.info("Validate code for {}", email);
        final UserEntity userEntity = this.emailService.validateCode(code, email);
        this.loginService.updateUserStatus(StatusEnum.ACTIVE, userEntity);

        LOGGER.info("Code validate successfully for {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam("newPassword") final String newPassword,
                                               @RequestParam("email") final String email) {
        LOGGER.info("Resetting password for {}", email);
        this.loginService.resetPassword(newPassword, email);
        LOGGER.info("Password reset for {}", email);
        return ResponseEntity.noContent().build();
    }

}
