package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.ResetPasswordDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountControllerTest {

    @Mock
    private LoginServiceApi loginService;

    @InjectMocks
    private AccountController accountController;

    @Mock
    private LogServiceApi logService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should update account successfully")
    void testUpdateAccount() throws BadRequestException, MessagingException, UnsupportedEncodingException {
        UpdateAccountRequestDto updateDto = new UpdateAccountRequestDto();
        Long userId = 1L;

        ResponseEntity<Object> response = accountController.updateAccount(userId, updateDto, httpServletRequest);

        assertNotNull(response);
        DefaultResponse body = (DefaultResponse) response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("Account updated successfully", body.getMessage());
        assertEquals(DefaultResponse.SUCCESS, body.getStatus());
        verify(loginService, times(1)).updateAccount(updateDto, userId, httpServletRequest);
    }

    @Test
    @DisplayName("Should logout successfully")
    void testLogout() {
        Long userId = 1L;

        ResponseEntity<Object> response = accountController.logout(userId, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loginService, times(1)).logout(userId, httpServletRequest);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        ResponseEntity<Object> response = accountController.deleteUser(1L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loginService, times(1)).deleteUser(1L, httpServletRequest);
    }

    @Test
    @DisplayName("Should get my logs successfully")
    void testGetMyLogs() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testuser");

        LogDto logDto = new LogDto(
                1L,
                "LOGIN",
                "User logged in",
                "127.0.0.1",
                LocalDateTime.now(),
                1L,
                "testuser",
                "Chrome"
        );

        Page<LogDto> logsPage = new PageImpl<>(Collections.singletonList(logDto));
        when(logService.getLogs(0, 10, "description", "asc", 1L, null, "testuser"))
                .thenReturn(logsPage);

        ResponseEntity<Object> response = accountController.getMyLogs(0, 10, "description", "asc", userDetails);

        //Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logsPage, response.getBody());
        verify(logService, times(1)).getLogs(0, 10, "description", "asc", 1L, null, "testuser");
    }


    @Test
    @DisplayName("Should get own user data successfully")
    void testGetMyUserData() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        UserVO userVO = new UserVO();
        when(loginService.getUserByUsername("testuser")).thenReturn(userVO);
        ResponseEntity<Object> response = accountController.getMyUserData(userDetails);

        //Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userVO, response.getBody());
        verify(loginService, times(1)).getUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should reset password successfully")
    void testResetPassword() {
        final ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .newPassword("newPass123")
                .email("test@example.com").build();

        ResponseEntity<Object> response = accountController.resetPassword(resetPasswordDto, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loginService, times(1)).resetPassword(resetPasswordDto.getNewPassword(), resetPasswordDto.getEmail()
                , resetPasswordDto.getUser(), httpServletRequest);
    }

}