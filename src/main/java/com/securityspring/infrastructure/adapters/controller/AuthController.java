package com.securityspring.infrastructure.adapters.controller;

import com.securityspring.application.service.api.JwtServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.infrastructure.adapters.api.AuthApi;
import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        final UserVO user = this.loginService.getUserByUsername(jwtServiceApi.extractUsername(request.getRefreshToken()));
        return this.jwtServiceApi.refreshToken(request, user);
    }
}