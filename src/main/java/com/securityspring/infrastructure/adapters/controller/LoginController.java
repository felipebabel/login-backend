package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.JwtServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.api.LoginApi;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.ResetPasswordDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
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

    private final JwtServiceApi jwtService;

    static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController(final LoginServiceApi loginService,
                           final EmailServiceApi emailService,
                           final JwtServiceApi jwtService) {
        this.loginService = loginService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Override
    @PostMapping
    public ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequestDto,
                                        final HttpServletRequest httpServletRequest) throws InterruptedException {
        final UserVO user = loginService.login(loginRequestDto, httpServletRequest);
        final TokenVO token = jwtService.generateToken(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
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
    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestParam("email") final String email,
                                            @RequestParam(value = "lang", required = false, defaultValue = "en") final String lang,
                                            final HttpServletRequest httpServletRequest) throws MessagingException {
        LOGGER.info("Sending email to {}", email);
        this.emailService.sendEmail(email, lang, httpServletRequest);
        LOGGER.info("Email sent to {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/validate-code")
    public ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                               @RequestParam("email") final String email,
                                               final HttpServletRequest httpServletRequest) {
        LOGGER.info("Validate code for {}", email);
        final UserVO user = this.emailService.validateCode(code, email, httpServletRequest);
        this.loginService.updateUserStatus(StatusEnum.ACTIVE, user);
        LOGGER.info("Code validate successfully for {}", email);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDto dto,
                                                final HttpServletRequest httpServletRequest) {
        LOGGER.info("Resetting password for email {}, or user: {}", dto.getEmail(), dto.getUser());
        this.loginService.resetPassword(dto.getNewPassword(), dto.getEmail(), dto.getUser(), httpServletRequest);
        LOGGER.info("Password reset for email {}, or user: {}", dto.getEmail(), dto.getUser());
        return ResponseEntity.noContent().build();
    }

}
