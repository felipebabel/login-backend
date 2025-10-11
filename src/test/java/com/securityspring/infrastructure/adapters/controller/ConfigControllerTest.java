package com.securityspring.infrastructure.adapters.controller;

import com.securityspring.application.service.api.ConfigServiceApi;
import com.securityspring.domain.exception.ConfigNotFoundException;
import com.securityspring.infrastructure.adapters.vo.ConfigVO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ConfigControllerTest {

    @Mock
    private ConfigServiceApi configService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ConfigController configController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should save configuration successfully")
    void testSetConfig() {
        ConfigVO expectedConfig = new ConfigVO();
        expectedConfig.setKey("test");
        expectedConfig.setValue("123");
        when(configService.setConfig(any(), any(), any(), eq(httpServletRequest)))
                .thenReturn(expectedConfig);

        ResponseEntity<Object> response = configController.setConfig(1L, "test", 1L, httpServletRequest);

        // Asserts
        assertNotNull(response);
        assertEquals(expectedConfig, response.getBody());
        verify(configService, times(1)).setConfig(any(), any(), any(), eq(httpServletRequest));
    }

    @Test
    @DisplayName("Should retrieve existing configuration successfully")
    void testGetConfig() throws ConfigNotFoundException {
        ConfigVO expectedConfig = new ConfigVO();
        expectedConfig.setKey("test");
        expectedConfig.setValue("123");

        when(configService.getConfig(any())).thenReturn(expectedConfig);

        ResponseEntity<Object> response = configController.getConfig("test");

        // Asserts
        assertNotNull(response);
        assertEquals(expectedConfig, response.getBody());
        verify(configService, times(1)).getConfig(any());
    }

    @Test
    @DisplayName("Should throw ConfigNotFoundException when configuration does not exist")
    void testGetConfigNotFound() throws ConfigNotFoundException {
        when(configService.getConfig(any())).thenThrow(new ConfigNotFoundException("Config not found"));

        try {
            configController.getConfig("non-existent-config");
        } catch (ConfigNotFoundException e) {
            assertEquals("Config not found", e.getMessage());
        }

        verify(configService, times(1)).getConfig(any());
    }
}