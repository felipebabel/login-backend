package com.securityspring.application.service;


import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.impl.EmailServiceImpl;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.CodeNotValidOrExpiredException;
import com.securityspring.domain.exception.UserNotFoundException;
import com.securityspring.domain.model.PasswordResetTokenEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.PasswordResetTokenRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.ProjectProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private ProjectProperties projectProperties;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LogServiceApi logService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EmailServiceImpl emailService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UserEntity();
        mockUser.setName("Felipe");
        mockUser.setEmail("felipe@test.com");
    }

    @Test
    @DisplayName("Should throw exception when user is not found during email sending")
    void shouldThrowExceptionWhenUserNotFoundOnSendEmail() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                emailService.sendEmail("unknown@test.com","en", request)
        );

        verify(userRepository).findByEmail("unknown@test.com");
        verifyNoInteractions(passwordResetTokenRepository, logService);
    }

    @Test
    @DisplayName("Should send verification email and save token when user exists")
    void shouldSendEmailAndSaveToken() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(projectProperties.getProperty("brevo.api-key")).thenReturn("dummyKey");
        when(projectProperties.getProperty("email-service.from")).thenReturn("noreply@test.com");

        emailService.sendEmail(mockUser.getEmail(), "en", request);

        verify(passwordResetTokenRepository).save(any(PasswordResetTokenEntity.class));
        verify(logService).setLog(eq("EMAIL SENT"), contains(mockUser.getEmail()), eq(request));
    }

    @Test
    @DisplayName("Should generate a random 6-digit numeric code")
    void shouldGenerateCodeWithinRange() {
        String code = emailService.generateCode();
        assertEquals(6, code.length());
        int numericCode = Integer.parseInt(code);
        assertTrue(numericCode >= 100000 && numericCode <= 999999);
    }

    @Test
    @DisplayName("Should validate code successfully and deactivate token")
    void shouldValidateCodeSuccessfully() {
        String code = "123456";
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setStatus(StatusEnum.ACTIVE);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        token.setUser(mockUser);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordResetTokenRepository.findValidToken(eq(mockUser), eq(code), eq(StatusEnum.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(token));

        UserVO result = emailService.validateCode(code, mockUser.getEmail(), request);

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(passwordResetTokenRepository).save(argThat(t -> t.getStatus() == StatusEnum.INACTIVE));
        verify(logService).setLog(eq("CODE VALIDATED"), contains(mockUser.getEmail()), eq(request));
    }

    @Test
    @DisplayName("Should throw exception when code is invalid or expired")
    void shouldThrowExceptionWhenCodeInvalidOrExpired() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordResetTokenRepository.findValidToken(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(CodeNotValidOrExpiredException.class, () ->
                emailService.validateCode("111111", mockUser.getEmail(), request)
        );
    }

    @Test
    @DisplayName("Should return number of deleted old tokens")
    void shouldDeleteOldTokens() {
        when(passwordResetTokenRepository.deleteOldTokens(any())).thenReturn(5);
        int deleted = emailService.deleteOldTokens();
        assertEquals(5, deleted);
        verify(passwordResetTokenRepository).deleteOldTokens(any());
    }

    @Test
    @DisplayName("Should send registration email with activation link and code")
    void shouldSendEmailOnRegistrationFlow() throws UnsupportedEncodingException {
        when(projectProperties.getProperty("front-end.url")).thenReturn("http://localhost:5173");
        when(projectProperties.getProperty("brevo.api-key")).thenReturn("dummyKey");
        when(projectProperties.getProperty("email-service.from")).thenReturn("noreply@test.com");

        emailService.sendEmail(mockUser, "en", request);

        verify(passwordResetTokenRepository).save(any(PasswordResetTokenEntity.class));
        verify(logService).setLog(eq("EMAIL SENT"), contains(mockUser.getEmail()), eq(request));
    }
}
