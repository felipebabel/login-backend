package com.securityspring.application.service;

import com.securityspring.application.service.impl.PasswordServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @Test
    @DisplayName("Should encrypt the raw password using PasswordEncoder")
    void testEncryptPassword_Success() {
        final String rawPassword = "mySecurePassword";
        final String encryptedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz.123456";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encryptedPassword);

        String result = passwordService.encryptPassword(rawPassword);

        // Assert
        assertEquals(encryptedPassword, result, "The returned string should be the encrypted version.");
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("Should return true when raw password matches the encoded password")
    void testCheckPassword_Match() {
        final String rawPassword = "correctPassword";
        final String encodedPassword = "$2a$10$zyxwvwvutstsrqponmlkjihgfedcba.789012";

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        boolean result = passwordService.checkPassword(rawPassword, encodedPassword);

        // Assert
        assertTrue(result, "The method should return true for a successful match.");
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Should return false when raw password does not match the encoded password")
    void testCheckPassword_NoMatch() {
        final String rawPassword = "wrongPassword";
        final String encodedPassword = "$2a$10$zyxwvwvutstsrqponmlkjihgfedcba.789012";

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = passwordService.checkPassword(rawPassword, encodedPassword);

        // Assert
        assertFalse(result, "The method should return false for a failed match.");
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}