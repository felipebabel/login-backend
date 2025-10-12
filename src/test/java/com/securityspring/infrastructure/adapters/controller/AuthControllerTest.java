package com.securityspring.infrastructure.adapters.controller;

import com.securityspring.application.service.api.JwtServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private JwtServiceApi jwtServiceApi;

    @Mock
    private LoginServiceApi loginService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshToken_Success() {
        final String refreshToken = "valid-refresh-token";
        final RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken(refreshToken);

        final UserVO mockUser = new UserVO();
        mockUser.setUsername("testUser");

        final TokenVO expectedToken = new TokenVO();
        expectedToken.setToken("new-access-token");
        expectedToken.setRefreshToken("new-refresh-token");

        when(jwtServiceApi.extractUsername(refreshToken)).thenReturn("testUser");
        when(loginService.getUserByUsername("testUser")).thenReturn(mockUser);
        when(jwtServiceApi.refreshToken(request, mockUser)).thenReturn(ResponseEntity.ok(expectedToken));

        final ResponseEntity<TokenVO> response = authController.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(ResponseEntity.ok(expectedToken), response);
        assertEquals(expectedToken, response.getBody());

        verify(jwtServiceApi, times(1)).extractUsername(refreshToken);
        verify(loginService, times(1)).getUserByUsername("testUser");
        verify(jwtServiceApi, times(1)).refreshToken(request, mockUser);
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when user not found")
    void testRefreshToken_UserNotFound_ReturnsUnauthorized() {
        final String refreshToken = "unknown-user-token";
        final RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken(refreshToken);

        when(jwtServiceApi.extractUsername(refreshToken)).thenReturn("unknownUser");
        when(loginService.getUserByUsername("unknownUser")).thenReturn(null);

        final ResponseEntity<TokenVO> unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        when(jwtServiceApi.refreshToken(request, null)).thenReturn(unauthorizedResponse);

        final ResponseEntity<TokenVO> response = authController.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        verify(jwtServiceApi, times(1)).extractUsername(refreshToken);
        verify(loginService, times(1)).getUserByUsername("unknownUser");
        verify(jwtServiceApi, times(1)).refreshToken(request, null);
    }
}

