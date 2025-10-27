package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.api.AccountApi;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.ResetPasswordDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController implements AccountApi {

    private final LogServiceApi logService;

    private final LoginServiceApi loginService;

    static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    public AccountController(final LogServiceApi logService,
                             final LoginServiceApi loginService) {
        this.logService = logService;
        this.loginService = loginService;
    }


    @Override
    @PreAuthorize("hasAnyRole('USER', 'ANALYST')")
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ANALYST')")
    @PutMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("user") final Long userIdentifier,
                                         final HttpServletRequest httpServletRequest) {
        LOGGER.info("User logging out");
        this.loginService.logout(userIdentifier, httpServletRequest);
        LOGGER.info("User logged out");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete-user")
    public ResponseEntity<Object> deleteUser(@RequestParam("user") Long userIdentifier,
                                             final HttpServletRequest httpServletRequest) {
        this.loginService.deleteUser(userIdentifier, httpServletRequest);
        LOGGER.info("User deleted successful");
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("my-logs")
    @PreAuthorize("hasAnyRole('ANALYST', 'USER')")
    public ResponseEntity<Object> getMyLogs(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "description") String sortBy,
                                            @RequestParam(defaultValue = "asc") String direction,
                                            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        final Page<LogDto> logs = logService.getLogs(
                page, size, sortBy, direction,
                userDetails.getId(),
                null,
                userDetails.getUsername()
        );
        LOGGER.info(logs.isEmpty() ? "Get my logs returned no content" : "Get my logs successful");
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN', 'USER')")
    @GetMapping("/get-my-user-data")
    public ResponseEntity<Object> getMyUserData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserVO user = loginService.getUserByUsername(userDetails.getUsername());
        LOGGER.info("Get own user data successfully for username: {}", userDetails.getUsername());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyRole('ANALYST', 'USER')")
    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDto dto,
                                                final HttpServletRequest httpServletRequest) {
        LOGGER.info("Resetting password for email {}, or user: {}", dto.getEmail(), dto.getUser());
        this.loginService.resetPassword(dto.getNewPassword(), dto.getEmail(), dto.getUser(), httpServletRequest);
        LOGGER.info("Password reset for email {}, or user: {}", dto.getEmail(), dto.getUser());
        return ResponseEntity.noContent().build();
    }
}
