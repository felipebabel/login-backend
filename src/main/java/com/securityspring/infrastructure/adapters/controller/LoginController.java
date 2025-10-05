package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.api.LoginApi;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.UserVO;
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
    public ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequestDto,
                                        final HttpServletRequest httpServletRequest) throws InterruptedException {
        final UserVO user = loginService.login(loginRequestDto, httpServletRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @PostMapping("/create-account")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody final CreateAccountRequestDto createAccount,
                                                final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException {
        LOGGER.info("Creating account");
        this.loginService.createAccount(createAccount, httpServletRequest);
        return new ResponseEntity<>(DefaultResponse.builder().message("Account created successfully")
                .status(DefaultResponse.SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Override
    @PutMapping("/update-account")
    public ResponseEntity<Object> updateAccount(@RequestParam("user") final Long userIdentifier,
                                                @Valid @RequestBody final UpdateAccountRequestDto account,
                                                final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException {
        LOGGER.info("Updating account");
        this.loginService.updateAccount(account, userIdentifier, httpServletRequest);
        return new ResponseEntity<>(DefaultResponse.builder().message("Account updated successfully")
                .status(DefaultResponse.SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Override
    @PutMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("user") final Long userIdentifier,
                                         final HttpServletRequest httpServletRequest) {
        LOGGER.info("User logging out");
        this.loginService.logout(userIdentifier, httpServletRequest);
        LOGGER.info("User logged out");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") final String email,
                                            final HttpServletRequest httpServletRequest) throws MessagingException {
        LOGGER.info("Sending email to {}", email);
        this.emailService.sendEmail(email, httpServletRequest);
        LOGGER.info("Email sent to {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/validate-code")
    public ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                               @RequestParam("email") final String email,
                                               final HttpServletRequest httpServletRequest) {
        LOGGER.info("Validate code for {}", email);
        this.emailService.validateCode(code, email, httpServletRequest);
        LOGGER.info("Code validate successfully for {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam("newPassword") final String newPassword,
                                               @RequestParam(value = "email", required = false) final String email,
                                                @RequestParam(value = "user", required = false) final String user,
                                                final HttpServletRequest httpServletRequest) {
        LOGGER.info("Resetting password for email {}, or user: {}", email, user);
        this.loginService.resetPassword(newPassword, email, user, httpServletRequest);
        LOGGER.info("Password reset for email {}, or user: {}", email, user);
        return ResponseEntity.noContent().build();
    }

}
