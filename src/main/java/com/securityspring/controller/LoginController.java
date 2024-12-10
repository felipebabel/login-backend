package com.securityspring.controller;

import com.securityspring.service.LoginService;
import com.securityspring.service.PasswordService;
import com.securityspring.util.DefaultResponse;
import com.securityspring.util.User;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController implements LoginApi{

    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";
    public static final String LOGIN_FAILED = "Login failed";
    private final PasswordService passwordService;
    private final LoginService loginService;

    static final Logger LOGGER = LoggerFactory.getLogger("PasswordController");

    public LoginController(PasswordService passwordService, LoginService loginService) {
        this.passwordService = passwordService;
        this.loginService = loginService;
    }

    @Override
    @PostMapping()
    public ResponseEntity<DefaultResponse> login(@RequestParam("user") final String user,
                                                 @RequestParam("password") final String password) throws BadRequestException {
        LOGGER.info("Starting login");
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) {
            final String decryptedPassword = this.passwordService.decryptPassword(optionalUser.get().getPassword(), user);
            if (password.equals(decryptedPassword)) {
                return new ResponseEntity<>(DefaultResponse.builder().message("Login successful")
                        .build(), HttpStatus.OK);
            } else {
                LOGGER.info(LOGIN_FAILED);
                return new ResponseEntity<>(DefaultResponse.builder().message("Invalid password").status(ERROR)
                        .build(), HttpStatus.BAD_REQUEST);
            }
        }
        LOGGER.info(LOGIN_FAILED);
        return new ResponseEntity<>(DefaultResponse.builder().message("User not found: " + user).status(ERROR)
                .build(), NOT_FOUND);
    }

    @Override
    @PostMapping("/create-account")
    public ResponseEntity<DefaultResponse> createAccount(@RequestParam("user") final String user,
                                                           @RequestParam("password") final String password) throws BadRequestException {
        LOGGER.info("Creating account");
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) {
            LOGGER.info("Account creation failed");
            return new ResponseEntity<>(DefaultResponse.builder().message("User already exists. User: " + optionalUser.get().getUsername())
            .status(ERROR)
                    .build(), HttpStatus.BAD_REQUEST);
        }
        final String encryptedPassword = this.passwordService.encryptPassword(password, user);
        this.loginService.saveUser(encryptedPassword, user);
        LOGGER.info("Account created successfully");
        return new ResponseEntity<>(DefaultResponse.builder().message("Account created successfully")
                .build(), HttpStatus.OK);
    }
}
