package com.securityspring.infrastructure.adapters.controller;

import java.util.Objects;
import com.securityspring.application.service.api.JwtServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.infrastructure.adapters.api.AuthApi;
import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final JwtServiceApi jwtServiceApi;

    private final LoginServiceApi loginService;

    @Autowired
    public AuthController(JwtServiceApi jwtServiceApi, LoginServiceApi loginService) {
        this.jwtServiceApi = jwtServiceApi;
        this.loginService = loginService;
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenVO> refreshToken(@RequestBody RefreshTokenRequestDto request) {

        if (Objects.isNull(request) || Objects.isNull(request.getRefreshToken())
                || request.getRefreshToken().trim().isEmpty()) {
            LOGGER.warn("Invalid refresh token request: missing or empty token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final UserVO user = this.loginService.getUserByUsername(jwtServiceApi.extractUsername(request.getRefreshToken()));

        final String oldRefreshToken = request.getRefreshToken();
        final TokenVO newTokenVO = jwtServiceApi.refreshToken(oldRefreshToken, user);
        if (Objects.nonNull(newTokenVO)) {
            LOGGER.info("Token successfully refreshed for user: {}", jwtServiceApi.extractUsername(oldRefreshToken));
            return ResponseEntity.ok(newTokenVO);
        } else {
            LOGGER.warn("Token refresh failed: refresh token expired or invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}