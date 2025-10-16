package com.securityspring.infrastructure.adapters.controller;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountControllerTest {

    @Mock
    private LoginServiceApi loginService;

    @InjectMocks
    private AccountController accountController;


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

}