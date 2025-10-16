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
import com.securityspring.infrastructure.adapters.api.AccountApi;
import com.securityspring.infrastructure.adapters.api.LoginApi;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController implements AccountApi {

    private final LoginServiceApi loginService;


    static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    public AccountController(LoginServiceApi loginService) {
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
}
