package com.securityspring.infrastructure.config;

import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AccountManagerScheduleTest {

    @Mock
    private LogServiceApi logService;

    @Mock
    private LoginServiceApi loginService;

    @Mock
    private EmailServiceApi emailServiceApi;

    @InjectMocks
    private AccountManagerSchedule accountManagerSchedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunDailyDeletion() {
        final UserVO admin = new UserVO();
        admin.setIdentifier(1L);

        when(loginService.getUserByUsername("admin")).thenReturn(admin);
        when(logService.deleteOldLogs()).thenReturn(5);
        when(loginService.deactivateUsers()).thenReturn(2);
        when(emailServiceApi.deleteOldTokens()).thenReturn(3);

        accountManagerSchedule.runDailyDeletion();

        verify(loginService).getUserByUsername("admin");
        verify(logService).deleteOldLogs();
        verify(logService, times(1)).setLog(any(), any(), contains("Total logs deleted"));
        verify(loginService).deactivateUsers();
        verify(logService, times(1)).setLog(any(), any(), contains("Account status update completed"));
        verify(logService, times(1)).setLog(any(), any(), contains("Account deletion completed"));
        verify(emailServiceApi).deleteOldTokens();
    }
}
