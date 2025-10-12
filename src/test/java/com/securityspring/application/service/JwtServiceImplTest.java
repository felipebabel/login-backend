package com.securityspring.application.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.securityspring.application.service.api.ConfigServiceApi;
import com.securityspring.application.service.impl.JwtServiceImpl;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.ProjectProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private ProjectProperties projectProperties;
    private ConfigServiceApi configService;

    @BeforeEach
    void setUp() {
        projectProperties = mock(ProjectProperties.class);
        configService = mock(ConfigServiceApi.class);
        when(projectProperties.getProperty("jwt.secret"))
                .thenReturn("ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXlmb3J0ZXN0aW5n");
        jwtService = new JwtServiceImpl(projectProperties, configService);
    }

    @Test
    @DisplayName("Should generate access and refresh token for valid user")
    void shouldGenerateToken() throws Exception {
        UserVO userVO = UserVO.builder().username("testuser").role(RolesUserEnum.USER).build();
        ConfigVO configVO = new ConfigVO();
        configVO.setValue("10");
        when(configService.getConfig("tokenSessionExpiration")).thenReturn(configVO);

        TokenVO tokenVO = jwtService.generateToken(userVO);

        assertNotNull(tokenVO.getToken());
        assertNotNull(tokenVO.getRefreshToken());
        assertTrue(tokenVO.getExpiresIn() > 0);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() throws Exception {
        UserVO userVO = UserVO.builder().username("testuser").role(RolesUserEnum.USER).build();
        ConfigVO configVO = new ConfigVO();
        configVO.setValue("10");
        when(configService.getConfig("tokenSessionExpiration")).thenReturn(configVO);

        TokenVO originalToken = jwtService.generateToken(userVO);

        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setRefreshToken(originalToken.getRefreshToken());

        ResponseEntity<TokenVO> response = jwtService.refreshToken(refreshRequest, userVO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(originalToken.getRefreshToken(), response.getBody().getRefreshToken());
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateToken() throws Exception {
        UserVO userVO = UserVO.builder().username("testuser").role(RolesUserEnum.USER).build();
        ConfigVO configVO = new ConfigVO();
        configVO.setValue("10");
        when(configService.getConfig("tokenSessionExpiration")).thenReturn(configVO);

        TokenVO tokenVO = jwtService.generateToken(userVO);

        UserDetails userDetails = User.withUsername("testuser").password("password").authorities(Collections.emptyList()).build();

        assertTrue(jwtService.isTokenValid(tokenVO.getToken(), userDetails));
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsername() throws Exception {
        UserVO userVO = UserVO.builder().username("testuser").role(RolesUserEnum.USER).build();
        ConfigVO configVO = new ConfigVO();
        configVO.setValue("10");
        when(configService.getConfig("tokenSessionExpiration")).thenReturn(configVO);

        TokenVO tokenVO = jwtService.generateToken(userVO);

        String username = jwtService.extractUsername(tokenVO.getToken());

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract role claim from token")
    void shouldExtractClaimByName() throws Exception {
        UserVO userVO = UserVO.builder().username("testuser").role(RolesUserEnum.USER).build();
        ConfigVO configVO = new ConfigVO();
        configVO.setValue("10");
        when(configService.getConfig("tokenSessionExpiration")).thenReturn(configVO);

        TokenVO tokenVO = jwtService.generateToken(userVO);

        String role = jwtService.extractClaimByName(tokenVO.getToken(), "role");

        assertEquals("USER", role);
    }
}
