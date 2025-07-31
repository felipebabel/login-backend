package com.securityspring.controller;

import com.securityspring.service.LoginService;
import com.securityspring.service.PasswordService;
import com.securityspring.util.DefaultResponse;
import com.securityspring.util.EmailDto;
import com.securityspring.util.User;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController implements LoginApi{

    public static final String ERROR = "ERROR";
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
    //@CrossOrigin(origins = "${FRONT_END_URL}")
    public ResponseEntity<DefaultResponse> login(@RequestParam("user") final String user,
                                                 @RequestParam("password") final String password) throws BadRequestException {
        LOGGER.info("Starting login");
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) { // todo move to the service
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
                                                           @RequestParam("password") final String password,
                                                         @RequestParam("email") final String email,
                                                         @RequestParam("first-name") final String name) throws BadRequestException {
        LOGGER.info("Creating account");
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) { // todo move to the service
            LOGGER.info("Account creation failed");
            return new ResponseEntity<>(DefaultResponse.builder().message("User already exists. User: " + optionalUser.get().getUsername())
            .status(ERROR)
                    .build(), HttpStatus.BAD_REQUEST);
        }
        final String encryptedPassword = this.passwordService.encryptPassword(password, user);
        this.loginService.saveUser(encryptedPassword, user, email, name);
        LOGGER.info("Account created successfully");
        return new ResponseEntity<>(DefaultResponse.builder().message("Account created successfully")
                .build(), HttpStatus.OK);
    }

    @Override
    @PutMapping("/forgot-password")
    public ResponseEntity<DefaultResponse> forgotPassword(@RequestParam("user") final String user,
                                                         @RequestParam("password") final String password) throws BadRequestException {
        LOGGER.info("Updating password");
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) { // todo move to the service
            LOGGER.info("User found. User: {}", user);
            final String encryptedPassword = this.passwordService.encryptPassword(password, user);
            this.loginService.updatePassword(encryptedPassword, optionalUser.get());
            LOGGER.info("Password updated successfully");
            return new ResponseEntity<>(DefaultResponse.builder().message("Password updated successfully")
                    .build(), HttpStatus.OK);
        }
        LOGGER.info("User not found. User {}", user);
        return new ResponseEntity<>(DefaultResponse.builder().message("User not found: " + user).status(ERROR)
                .build(), NOT_FOUND);
    }

    @Override
    @PutMapping("/requestPasswordReset")
    public ResponseEntity<DefaultResponse> requestPasswordReset(@RequestParam("user") final String user) {
        LOGGER.info("Requesting password reset for user: {}", user);
        final Optional<User> optionalUser = this.loginService.findUser(user);
        if (optionalUser.isPresent()) {
            return this.loginService.requestPasswordReset(optionalUser.get().getFirstName(), optionalUser.get().getEmail());
        }
        LOGGER.info("User not found. User {}", user);
        return new ResponseEntity<>(DefaultResponse.builder().message("User not found: " + user).status(ERROR)
                .build(), NOT_FOUND);
    }


}
