package com.securityspring.application.service;

import com.securityspring.application.service.api.*;
import com.securityspring.application.service.impl.LoginServiceImpl;
import com.securityspring.domain.enums.*;
import com.securityspring.domain.exception.*;
import com.securityspring.domain.model.*;
import com.securityspring.domain.port.*;
import com.securityspring.infrastructure.adapters.dto.*;
import com.securityspring.infrastructure.adapters.vo.*;
import com.securityspring.infrastructure.config.ProjectProperties;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LoginServiceImpl Unit Tests")
class LoginServiceImplTest {

    @Mock
    private ProjectProperties projectProperties;

    @Mock
    private PasswordServiceApi passwordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LogServiceApi logService;

    @Mock
    private ConfigRepository configRepository;

    @Mock
    private EmailServiceApi emailService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private LoginServiceImpl loginService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntity = new UserEntity();
        userEntity.setIdentifier(1L);
        userEntity.setUsername("user");
        userEntity.setPassword("encrypted");
        userEntity.setStatus(StatusEnum.ACTIVE);
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        userEntity.setLoginAttempt(0);
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        UpdateAccountRequestDto dto = new UpdateAccountRequestDto();
        dto.setUser("newUser");
        dto.setName("New Name");
        dto.setLanguage("EN");
        dto.setGender("MALE");

        when(userRepository.save(any())).thenReturn(userEntity);

        UserEntity result = loginService.updateUser(userEntity, dto);
        assertEquals("newUser", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Should save new user successfully")
    void shouldSaveUser() {
        when(userRepository.save(any())).thenReturn(userEntity);

        UserEntity result = loginService.saveUser(
                "pass", "user", "email@test.com", "User",
                StatusEnum.ACTIVE, RolesUserEnum.USER, LanguagesEnum.EN
        );
        assertEquals("user", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Should inactivate existing user")
    void shouldInactivateAccount() {
        when(userRepository.findByIdentifier(1L)).thenReturn(Optional.of(userEntity));
        UserVO result = loginService.inactiveAccount(1L, httpRequest);
        assertEquals(StatusEnum.INACTIVE, userEntity.getStatus());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when inactivating non-existent user")
    void shouldThrowWhenInactivatingNonexistentUser() {
        when(userRepository.findByIdentifier(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> loginService.inactiveAccount(1L, httpRequest));
    }

    @Test
    @DisplayName("Should force password change successfully")
    void shouldForcePasswordChange() {
        when(userRepository.findByIdentifier(1L)).thenReturn(Optional.of(userEntity));
        UserVO result = loginService.forcePasswordChange(1L, httpRequest);
        assertTrue(userEntity.isForcePasswordChange());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUser("user");
        dto.setPassword("pass");

        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(1L);
        userEntity.setUsername("user");
        userEntity.setPassword("encrypted");
        userEntity.setStatus(StatusEnum.ACTIVE);
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        userEntity.setLoginAttempt(0);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userEntity));
        when(configRepository.findByKey("passwordExpiryDays")).thenReturn(Optional.empty());
        when(passwordService.checkPassword(any(), any())).thenReturn(true);

        UserVO result = loginService.login(dto, httpRequest);

        // Assert
        assertNotNull(result);
        assertEquals("user", result.getUsername());
        verify(logService, atLeastOnce())
                .setLog(any(String.class), anyLong(), any(HttpServletRequest.class));
        verify(userRepository, times(2)).save(any(UserEntity.class));
    }


    @Test
    @DisplayName("Should throw PasswordExpiredException if password expired")
    void shouldThrowPasswordExpiredException() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUser("user");
        dto.setPassword("pass");

        userEntity.setPasswordChangeDate(LocalDateTime.now().minusDays(100));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userEntity));
        when(configRepository.findByKey("passwordExpiryDays"))
                .thenReturn(Optional.of(new ConfigEntity(1L, "passwordExpiryDays", "90", LocalDateTime.now())));

        assertThrows(PasswordExpiredException.class, () -> loginService.login(dto, httpRequest));
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password is wrong")
    void shouldThrowInvalidPasswordException() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUser("user");
        dto.setPassword("wrong");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userEntity));
        when(configRepository.findByKey("passwordExpiryDays")).thenReturn(Optional.empty());
        when(passwordService.checkPassword(any(), any())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> loginService.login(dto, httpRequest));
    }

    @Test
    @DisplayName("Should create new account successfully")
    void shouldCreateAccount() throws MessagingException, UnsupportedEncodingException {
        CreateAccountRequestDto dto = new CreateAccountRequestDto();
        dto.setUser("newUser");
        dto.setPassword("12345678");
        dto.setEmail("email@test.com");
        dto.setName("User");
        dto.setLanguage("EN");

        UserEntity userEntity = new UserEntity();
        userEntity.setIdentifier(1L);

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordService.encryptPassword(any())).thenReturn("encrypted");

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setIdentifier(1L);
            return u;
        });

        loginService.createAccount(dto, httpRequest);

        verify(emailService).sendEmail(any(UserEntity.class), any(HttpServletRequest.class));
        verify(logService).setLog(eq("CREATED ACCOUNT"), eq(1L), eq(httpRequest));
    }



    @Test
    @DisplayName("Should throw UserAlreadyExistsException on createAccount")
    void shouldThrowUserAlreadyExists() {
        CreateAccountRequestDto dto = new CreateAccountRequestDto();
        dto.setUser("existing");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(userEntity));
        assertThrows(UserAlreadyExistsException.class, () -> loginService.createAccount(dto, httpRequest));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void shouldResetPasswordSuccessfully() {
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(userEntity));
        when(passwordService.encryptPassword(any())).thenReturn("newEncrypted");
        when(passwordService.checkPassword(any(), any())).thenReturn(false);

        loginService.resetPassword("newPass123", "mail@test.com", null, httpRequest);

        verify(userRepository).save(userEntity);
        assertEquals(LocalDate.now(), userEntity.getPasswordChangeDate());
    }

    @Test
    @DisplayName("Should throw BadRequestException when new password is too short")
    void shouldThrowBadRequestExceptionOnShortPassword() {
        assertThrows(BadRequestException.class, () -> loginService.resetPassword("123", "a@a.com", null, httpRequest));
    }

    @Test
    @DisplayName("Should delete existing user")
    void shouldDeleteUser() {
        when(userRepository.findByIdentifier(1L)).thenReturn(Optional.of(userEntity));
        loginService.deleteUser(1L, httpRequest);
        verify(userRepository).delete(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.findByIdentifier(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> loginService.deleteUser(1L, httpRequest));
    }

    @Test
    @DisplayName("Should return total account data")
    void shouldReturnTotalAccount() {
        TotalAccountProjection projection = mock(TotalAccountProjection.class);
        when(userRepository.getTotalAccount()).thenReturn(projection);
        assertNotNull(loginService.getTotalAccount());
    }

    @Test
    @DisplayName("Should create default users when not existing")
    void shouldCreateDefaultUsers() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordService.encryptPassword(any())).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(userEntity);
        loginService.createDefaultUsers();
        verify(userRepository, atLeast(3)).save(any());
    }

    @Test
    @DisplayName("Should not create default users when they exist")
    void shouldNotCreateDefaultUsersWhenExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        loginService.createDefaultUsers();
        verify(userRepository, never()).save(any());
    }
}
