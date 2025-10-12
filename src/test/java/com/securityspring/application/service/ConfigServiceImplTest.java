package com.securityspring.application.service;


import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.impl.ConfigServiceImpl;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.domain.port.ConfigRepository;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigServiceImplTest {

    @Mock
    private ConfigRepository configRepository;

    @Mock
    private LogServiceApi logService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ConfigServiceImpl configService;

    private ConfigEntity configEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        configEntity = new ConfigEntity();
        configEntity.setIdentifier(1L);
        configEntity.setKey("email.host");
        configEntity.setValue("smtp.gmail.com");
        configEntity.setUpdateDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save a new configuration when key does not exist")
    void shouldSaveNewConfigurationWhenKeyDoesNotExist() {
        when(configRepository.findByKey("email.port")).thenReturn(Optional.empty());
        when(configRepository.save(any(ConfigEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfigVO result = configService.setConfig("email.port", "587", 1L, httpServletRequest);

        assertNotNull(result);
        assertEquals("email.port", result.getKey());
        assertEquals("587", result.getValue());
        verify(configRepository).save(any(ConfigEntity.class));
        verify(logService).setLog(eq("CONFIG"), contains("Setting key: email.port"), eq(httpServletRequest));
    }

    @Test
    @DisplayName("Should update existing configuration when key already exists")
    void shouldUpdateExistingConfigurationWhenKeyAlreadyExists() {
        when(configRepository.findByKey("email.host")).thenReturn(Optional.of(configEntity));
        when(configRepository.save(any(ConfigEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfigVO result = configService.setConfig("email.host", "smtp.outlook.com", 2L, httpServletRequest);

        assertEquals("email.host", result.getKey());
        assertEquals("smtp.outlook.com", result.getValue());
        verify(configRepository).save(any(ConfigEntity.class));
        verify(logService).setLog(eq("CONFIG"), contains("smtp.outlook.com"), eq(httpServletRequest));
    }

    @Test
    @DisplayName("Should return existing configuration when key is found")
    void shouldReturnExistingConfigurationWhenKeyIsFound() throws ConfigNotFoundException {
        when(configRepository.findByKey("email.host")).thenReturn(Optional.of(configEntity));

        ConfigVO result = configService.getConfig("email.host");

        assertNotNull(result);
        assertEquals("email.host", result.getKey());
        assertEquals("smtp.gmail.com", result.getValue());
        verify(configRepository).findByKey("email.host");
    }

    @Test
    @DisplayName("Should throw ConfigNotFoundException when key does not exist")
    void shouldThrowConfigNotFoundExceptionWhenKeyDoesNotExist() {
        when(configRepository.findByKey("invalid.key")).thenReturn(Optional.empty());

        ConfigNotFoundException exception = assertThrows(
                ConfigNotFoundException.class,
                () -> configService.getConfig("invalid.key")
        );

        assertEquals("Not found configuration for key: invalid.key", exception.getMessage());
        verify(configRepository).findByKey("invalid.key");
    }
}
