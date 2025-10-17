package com.securityspring.infrastructure.adapters.controller;

import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.LoginAttemptsCountDTO;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
import com.securityspring.infrastructure.adapters.vo.TotalAccountVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private LoginServiceApi loginService;

    @Mock
    private LogServiceApi logService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should get pending accounts successfully")
    void testGetPendingAccount() {
        Page<UserVO> page = new PageImpl<>(Collections.singletonList(new UserVO()));
        when(loginService.getPendingUsers(0, 10, "creationUserDate", "asc")).thenReturn(page);

        ResponseEntity<Object> response = adminController.getPendingAccount(0, 10, "creationUserDate", "asc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    @DisplayName("Should update user role successfully")
    void testUpdateUserRole() {
        UserVO user = new UserVO();
        when(loginService.updateUserRole(1L, RolesUserEnum.ADMIN, 2L, httpServletRequest))
                .thenReturn(user);

        ResponseEntity<Object> response = adminController.updateUserRole(1L, "ADMIN", 2L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should activate user successfully")
    void testActiveUser() {
        UserVO user = new UserVO();
        when(loginService.activeUser(1L, httpServletRequest)).thenReturn(user);

        ResponseEntity<Object> response = adminController.activeUser(1L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should block user successfully")
    void testBlockUser() {
        UserVO user = new UserVO();
        when(loginService.blockUser(1L, httpServletRequest)).thenReturn(user);

        ResponseEntity<Object> response = adminController.blockUser(1L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        ResponseEntity<Object> response = adminController.deleteUser(1L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loginService, times(1)).deleteUser(1L, httpServletRequest);
    }

    @Test
    @DisplayName("Should reset user password successfully")
    void testForcePasswordChange() {
        UserVO user = new UserVO();
        when(loginService.forcePasswordChange(1L, httpServletRequest)).thenReturn(user);

        ResponseEntity<Object> response = adminController.forcePasswordChange(1L, httpServletRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should get total account successfully")
    void testGetTotalAccount() throws BadRequestException {
        TotalAccountVO total = new TotalAccountVO();
        when(loginService.getTotalAccount()).thenReturn(total);

        ResponseEntity<Object> response = adminController.getTotalAccount();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(total, response.getBody());
    }

    @Test
    @DisplayName("Should get login attempts successfully")
    void testGetLoginAttempts() {
        List<LoginAttemptsCountDTO> attempts = Collections.singletonList(new LoginAttemptsCountDTO("LOGIN ATTEMPT OK", 1L));
        when(logService.getLoginAttempts()).thenReturn(attempts);

        ResponseEntity<Object> response = adminController.getLoginAttempts();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attempts, response.getBody());
    }

    @Test
    @DisplayName("Should get new users per month successfully")
    void testGetNewAccountMonth() {
        List<NewUsersPerMonthDTO> newUsers = Collections.singletonList(new NewUsersPerMonthDTO(10,10,10L));
        when(loginService.getNewAccountMonth()).thenReturn(newUsers);

        ResponseEntity<Object> response = adminController.getNewAccountMonth();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newUsers, response.getBody());
    }

    @Test
    @DisplayName("Should throw BadRequestException if no username or name provided")
    void testGetUserByUsernameBadRequest() {
        try {
            adminController.getUserByUsername(null, null);
        } catch (BadRequestException e) {
            assertEquals("You must provide either 'username' or 'name'", e.getMessage());
        }
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void testGetUserByUsername() throws BadRequestException {
        UserVO user = new UserVO();
        when(loginService.getUserByUsername("testuser")).thenReturn(user);

        ResponseEntity<Object> response = adminController.getUserByUsername("testuser", null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Should get user by name successfully")
    void testGetUserByName() throws BadRequestException {
        UserVO user = new UserVO();
        when(loginService.getUserByName("Test Name")).thenReturn(user);

        ResponseEntity<Object> response = adminController.getUserByUsername(null, "Test Name");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
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

        ResponseEntity<Object> response = adminController.getMyLogs(0, 10, "description", "asc", userDetails);

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
        ResponseEntity<Object> response = adminController.getMyUserData(userDetails);

        //Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userVO, response.getBody());
        verify(loginService, times(1)).getUserByUsername("testuser");
    }


}