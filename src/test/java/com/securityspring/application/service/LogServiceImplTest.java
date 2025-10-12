package com.securityspring.application.service;

import com.securityspring.application.service.impl.LogServiceImpl;
import com.securityspring.domain.model.LogsEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.LogRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.LoginAttemptsCountDTO;
import com.securityspring.infrastructure.config.ProjectProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @Mock
    private ProjectProperties projectProperties;

    @Mock
    private LogRepository logRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private LogServiceImpl logService;

    private UserEntity userEntity;
    private LogsEntity logsEntity;
    private final Long USER_ID = 10L;
    private final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setIdentifier(USER_ID);
        userEntity.setUsername(USERNAME);

        logsEntity = new LogsEntity();
        logsEntity.setIdentifier(1L);
        logsEntity.setAction("LOGIN");
        logsEntity.setDescription("User logged in");
        logsEntity.setIpAddress("127.0.0.1");
        logsEntity.setDate(LocalDateTime.now());
        logsEntity.setUser(userEntity);
        logsEntity.setDeviceName("Mozilla/5.0");
    }

    @Test
    @DisplayName("Should return a page of LogDto, sorting by date descending by default")
    void testGetLogs_DefaultSort() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        Page<LogsEntity> logsPage = new PageImpl<>(List.of(logsEntity), pageable, 1);

        when(logRepository.findLogs(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(logsPage);

        Page<LogDto> result = logService.getLogs(0, 10, "date", "desc", null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("LOGIN", result.getContent().get(0).getAction());
        verify(logRepository).findLogs(eq(null), eq(null), eq(null), argThat(p -> p.getSort().toString().contains("date: DESC")));
    }

    @Test
    @DisplayName("Should return a page of LogDto, sorting by username ascending")
    void testGetLogs_SortByUsernameAscending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("user.username").ascending());
        Page<LogsEntity> logsPage = new PageImpl<>(List.of(logsEntity), pageable, 1);

        when(logRepository.findLogs(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(logsPage);

        logService.getLogs(0, 10, "username", "asc", null, null, null);

        // Assert
        verify(logRepository).findLogs(eq(null), eq(null), eq(null), argThat(p -> p.getSort().toString().contains("user.username: ASC")));
    }

    @Test
    @DisplayName("Should return a page of LogDto, sorting by userIdentifier descending")
    void testGetLogs_SortByUserIdentifierDescending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("user.identifier").descending());
        Page<LogsEntity> logsPage = new PageImpl<>(List.of(logsEntity), pageable, 1);

        when(logRepository.findLogs(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(logsPage);

        // Act
        logService.getLogs(0, 10, "userIdentifier", "desc", null, null, null);

        // Assert
        verify(logRepository).findLogs(eq(null), eq(null), eq(null), argThat(p -> p.getSort().toString().contains("user.identifier: DESC")));
    }

    @Test
    @DisplayName("Should map LogsEntity with null user to LogDto with null user data")
    void testGetLogs_NullUser() {
        LogsEntity nullUserLog = new LogsEntity();
        nullUserLog.setIdentifier(2L);
        nullUserLog.setUser(null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<LogsEntity> logsPage = new PageImpl<>(List.of(nullUserLog), pageable, 1);

        when(logRepository.findLogs(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(logsPage);

        Page<LogDto> result = logService.getLogs(0, 10, "date", "asc", null, null, null);

        // Assert
        assertNull(result.getContent().get(0).getUserId());
        assertNull(result.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("Should return a list of LoginAttemptsCountDTO")
    void testGetLoginAttempts() {
        List<Object[]> loginAttempts = Arrays.asList(
                new Object[]{"ATTEMPT_IP_127.0.0.1", 5L},
                new Object[]{"ATTEMPT_IP_192.168.1.1", 2L}
        );
        when(logRepository.getLoginAttempts()).thenReturn(loginAttempts);

        List<LoginAttemptsCountDTO> result = logService.getLoginAttempts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ATTEMPT_IP_127.0.0.1", result.get(0).getAction());
        assertEquals(5L, result.get(0).getTotal());
        assertEquals("ATTEMPT_IP_192.168.1.1", result.get(1).getAction());
        assertEquals(2L, result.get(1).getTotal());

        verify(logRepository).getLoginAttempts();
    }

    @Test
    @DisplayName("Should save log with user details and request info")
    void testSetLog_WithUserAndRequest() {
        when(userRepository.findByIdentifier(USER_ID)).thenReturn(Optional.of(userEntity));
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Test-Agent");
        ArgumentCaptor<LogsEntity> logCaptor = ArgumentCaptor.forClass(LogsEntity.class);

        logService.setLog("ACTION_1", USER_ID, httpServletRequest);

        // Assert
        verify(logRepository).save(logCaptor.capture());
        LogsEntity savedLog = logCaptor.getValue();
        assertEquals("ACTION_1", savedLog.getAction());
        assertEquals("127.0.0.1", savedLog.getIpAddress());
        assertEquals("Test-Agent", savedLog.getDeviceName());
        assertEquals(userEntity, savedLog.getUser());
    }

    @Test
    @DisplayName("Should save log without user when user identifier is not found")
    void testSetLog_NoUserFoundAndRequest() {
        when(userRepository.findByIdentifier(USER_ID)).thenReturn(Optional.empty());
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        ArgumentCaptor<LogsEntity> logCaptor = ArgumentCaptor.forClass(LogsEntity.class);

        logService.setLog("ACTION_2", USER_ID, httpServletRequest);

        // Assert
        verify(logRepository).save(logCaptor.capture());
        LogsEntity savedLog = logCaptor.getValue();
        assertEquals("ACTION_2", savedLog.getAction());
        assertNull(savedLog.getUser());
    }

    @Test
    @DisplayName("Should save log with user details and description (no request)")
    void testSetLog_WithUserAndDescription() {
        when(userRepository.findByIdentifier(USER_ID)).thenReturn(Optional.of(userEntity));
        ArgumentCaptor<LogsEntity> logCaptor = ArgumentCaptor.forClass(LogsEntity.class);

        logService.setLog("ACTION_3", USER_ID, "Custom description");

        // Assert
        verify(logRepository).save(logCaptor.capture());
        LogsEntity savedLog = logCaptor.getValue();
        assertEquals("ACTION_3", savedLog.getAction());
        assertEquals("Custom description", savedLog.getDescription());
        assertEquals("", savedLog.getIpAddress()); // Check default empty string
        assertEquals(userEntity, savedLog.getUser());
        assertNull(savedLog.getDeviceName()); // Check default null
    }


    @Test
    @DisplayName("Should save log with description and request info (no user lookup)")
    void testSetLog_WithDescriptionAndRequest_NoUserLookup() {
        when(httpServletRequest.getRemoteAddr()).thenReturn("192.168.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome");
        ArgumentCaptor<LogsEntity> logCaptor = ArgumentCaptor.forClass(LogsEntity.class);

        logService.setLog("ACTION_4", USER_ID, "System event", httpServletRequest);

        // Assert
        verify(logRepository).save(logCaptor.capture());
        LogsEntity savedLog = logCaptor.getValue();
        assertEquals("ACTION_4", savedLog.getAction());
        assertEquals("System event", savedLog.getDescription());
        assertEquals("192.168.0.1", savedLog.getIpAddress());
        assertEquals("Chrome", savedLog.getDeviceName());
        verify(userRepository, never()).findByIdentifier(any());
    }


    @Test
    @DisplayName("Should save log with description and request info (no user id)")
    void testSetLog_WithDescriptionAndRequest_NoUserId() {
        when(httpServletRequest.getRemoteAddr()).thenReturn("10.0.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Safari");
        ArgumentCaptor<LogsEntity> logCaptor = ArgumentCaptor.forClass(LogsEntity.class);

        logService.setLog("ACTION_5", "Unauthorized access", httpServletRequest);

        // Assert
        verify(logRepository).save(logCaptor.capture());
        LogsEntity savedLog = logCaptor.getValue();
        assertEquals("ACTION_5", savedLog.getAction());
        assertEquals("Unauthorized access", savedLog.getDescription());
        assertEquals("10.0.0.1", savedLog.getIpAddress());
        assertEquals("Safari", savedLog.getDeviceName());
        assertNull(savedLog.getUser());
        verify(userRepository, never()).findByIdentifier(any());
    }

    @Test
    @DisplayName("Should call repository to delete logs older than 180 days")
    void testDeleteOldLogs() {
        final int deletedCount = 5;
        when(logRepository.deleteOldLogs(any(LocalDateTime.class))).thenReturn(deletedCount);

        int result = logService.deleteOldLogs();

        // Assert
        assertEquals(deletedCount, result);
        verify(logRepository).deleteOldLogs(any(LocalDateTime.class));
    }
}