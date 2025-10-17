package com.securityspring.infrastructure.adapters.controller;

import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.ResetPasswordDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private LoginServiceApi loginService;

    @Mock
    private EmailServiceApi emailService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin() throws InterruptedException {
        LoginRequestDto requestDto = new LoginRequestDto();
        UserVO expectedUser = new UserVO();
        expectedUser.setEmail("test@example.com");

        when(loginService.login(requestDto, httpServletRequest)).thenReturn(expectedUser);

        ResponseEntity<Object> response = loginController.login(requestDto, httpServletRequest);

        assertNotNull(response);
        assertEquals(expectedUser, response.getBody());
        verify(loginService, times(1)).login(requestDto, httpServletRequest);
    }

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccount() throws BadRequestException, MessagingException, UnsupportedEncodingException {
        CreateAccountRequestDto createAccountDto = new CreateAccountRequestDto();

        ResponseEntity<Object> response = loginController.createAccount(createAccountDto, httpServletRequest);

        assertNotNull(response);
        DefaultResponse body = (DefaultResponse) response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("Account created successfully", body.getMessage());
        assertEquals(DefaultResponse.SUCCESS, body.getStatus());
        verify(loginService, times(1)).createAccount(createAccountDto, httpServletRequest);
    }

    @Test
    @DisplayName("Should send email successfully")
    void testSendEmail() throws MessagingException {
        String email = "test@example.com";
        String lang = "en";
        ResponseEntity<Object> response = loginController.sendEmail(email, lang, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailService, times(1)).sendEmail(email, lang, httpServletRequest);
    }

    @Test
    @DisplayName("Should validate code successfully")
    void testValidateCode() {
        String email = "test@example.com";
        String code = "123456";
        UserVO user = new UserVO();

        when(emailService.validateCode(code, email, httpServletRequest)).thenReturn(user);

        ResponseEntity<Object> response = loginController.validateCode(code, email, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(emailService, times(1)).validateCode(code, email, httpServletRequest);
        verify(loginService, times(1)).updateUserStatus(StatusEnum.ACTIVE, user);
    }

    @Test
    @DisplayName("Should reset password successfully")
    void testResetPassword() {
        final ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .newPassword("newPass123")
                .email("test@example.com").build();

        ResponseEntity<Object> response = loginController.resetPassword(resetPasswordDto, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loginService, times(1)).resetPassword(resetPasswordDto.getNewPassword(), resetPasswordDto.getEmail()
                    , resetPasswordDto.getUser(), httpServletRequest);
    }
}