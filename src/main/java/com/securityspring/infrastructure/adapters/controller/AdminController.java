package com.securityspring.infrastructure.adapters.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.api.AdminApi;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.LoginAttemptsCountDTO;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
import com.securityspring.infrastructure.adapters.vo.TotalAccountVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminApi {

    private final LoginServiceApi loginService;

    private final LogServiceApi logService;

    static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(final LoginServiceApi loginService1,
                           final LogServiceApi logService) {
        this.loginService = loginService1;
        this.logService = logService;
    }

    @Override
    @GetMapping("get-pending-account")
    public ResponseEntity<Object> getPendingAccount(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        final Page<UserVO> users = loginService.getPendingUsers(page, size, sortBy, direction);
        LOGGER.info(users.isEmpty()
                ? "Get pending account returned no content"
                : "Get pending account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-logs")
    public ResponseEntity<Object> getLogs(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "description") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction,
                                                  @RequestParam(required = false) Long userIdentifier,
                                                    @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) String action
                                            ) {
        final Page<LogDto> users = logService.getLogs(page, size, sortBy, direction, userIdentifier, action, username);
        LOGGER.info(users.isEmpty()
                ? "Get pending account returned no content"
                : "Get pending account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-active-account")
    public ResponseEntity<Object> getActiveAccount(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String direction) {
        final Page<UserVO> users = loginService.getActiveUsers(page, size, sortBy, direction);
        LOGGER.info(users.isEmpty()
                ? "Get active account returned no content"
                : "Get active account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-blocked-account")
    public ResponseEntity<Object> getBlockedAccount(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        final Page<UserVO> users = loginService.getBlockedUsers(page, size, sortBy, direction);
        LOGGER.info(users.isEmpty()
                ? "Get blocked account returned no content"
                : "Get blocked account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-users")
    public ResponseEntity<Object> getUsers(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction,
                                           @RequestParam(required = false) Long userIdentifier,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String name) {
        final Page<UserVO> users = loginService.getUsers(page, size, sortBy, direction, userIdentifier, username, name);
        LOGGER.info(users.isEmpty()
                ? "Get blocked account returned no content"
                : "Get blocked account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @PutMapping("/update-role")
    public ResponseEntity<Object> updateUserRole(
            @RequestParam final Long userIdentifier,
            @RequestParam final String role,
            @RequestParam final Long userRequired,
            final HttpServletRequest httpServletRequest
    ) {
        final RolesUserEnum rolesUserEnum = RolesUserEnum.fromString(role);
        final UserVO user = loginService.updateUserRole(userIdentifier, rolesUserEnum, userRequired, httpServletRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-inactive-account")
    public ResponseEntity<Object> getInactiveAccount(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        final Page<UserVO> users = loginService.getInactiveUsers(page, size, sortBy, direction);
        LOGGER.info(users.isEmpty()
                ? "Get inactive account returned no content"
                : "Get inactive account successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-total-account")
    public ResponseEntity<Object> getTotalAccount() throws BadRequestException {
        final TotalAccountVO totalAccountVO = loginService.getTotalAccount();
        LOGGER.info("Get total account successfully");
        return new ResponseEntity<>(totalAccountVO, HttpStatus.OK);
    }

    @Override
    @PutMapping("allow-user")
    public ResponseEntity<Object> activeUser(@RequestParam("user") Long userIdentifier,
                                             final HttpServletRequest httpServletRequest) {
        final UserVO users = loginService.activeUser(userIdentifier, httpServletRequest);
        LOGGER.info("User account activated successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @PutMapping("block-user")
    public ResponseEntity<Object> blockUser(@RequestParam("user") Long userIdentifier,
                                             final HttpServletRequest httpServletRequest) {
        final UserVO user = loginService.blockUser(userIdentifier, httpServletRequest);
        LOGGER.info("User blocked successful");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("delete-user")
    public ResponseEntity<Object> deleteUser(@RequestParam("user") Long userIdentifier,
                                             final HttpServletRequest httpServletRequest) {
        this.loginService.deleteUser(userIdentifier, httpServletRequest);
        LOGGER.info("User deleted successful");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("inactive-user")
    public ResponseEntity<Object> inactiveUser(@RequestParam("user") Long userIdentifier,
                                               final HttpServletRequest httpServletRequest) {
        final UserVO userEntity = loginService.inactiveAccount(userIdentifier, httpServletRequest);
        LOGGER.info("Inactive account successful");
        return new ResponseEntity<>(userEntity, HttpStatus.OK);
    }

    @Override
    @PutMapping("force-password-change")
    public ResponseEntity<Object> forcePasswordChange(@RequestParam("user") Long userIdentifier,
                                             final HttpServletRequest httpServletRequest) {
        final UserVO userEntity = loginService.forcePasswordChange(userIdentifier, httpServletRequest);
        //todo invalidate token session
        LOGGER.info("Force password change for user {} successful", userIdentifier);
        return new ResponseEntity<>(userEntity, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-user-by-identifier")
    public ResponseEntity<Object> getUserByUsername(@RequestParam(value = "username", required = false) String username,
                                                    @RequestParam(value = "name", required = false) String name) throws BadRequestException {
        UserVO user;
        if (username != null && !username.isEmpty()) {
            user = loginService.getUserByUsername(username);
            LOGGER.info("Get user by username successfully.");
        } else if (name != null && !name.isEmpty()) {
            user = loginService.getUserByName(name);
            LOGGER.info("Get user by name successfully.");
        } else {
            throw new BadRequestException("You must provide either 'username' or 'name'");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-active-sessions")
    public ResponseEntity<Object> getActiveSession(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "creationUserDate") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        final Page<UserVO> users = loginService.getActiveSessions(page, size, sortBy, direction);
        LOGGER.info(users.isEmpty()
                ? "Get active sessions returned no content"
                : "Get active sessions successful");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-login-attempts")
    public ResponseEntity<Object> getLoginAttempts() {
        final List<LoginAttemptsCountDTO> loginAttempts = logService.getLoginAttempts();
        LOGGER.info(loginAttempts.isEmpty()
                ? "Get login attempts returned no content"
                : "Get login attempts successful");
        return new ResponseEntity<>(loginAttempts, HttpStatus.OK);
    }

    @Override
    @GetMapping("get-new-account-month")
    public ResponseEntity<Object> getNewAccountMonth() {
        final List<NewUsersPerMonthDTO> newUsersPerMonth = loginService.getNewAccountMonth();
        LOGGER.info(newUsersPerMonth.isEmpty()
                ? "Get new users per month returned no content"
                : "Get new users per month successful");
        return new ResponseEntity<>(newUsersPerMonth, HttpStatus.OK);
    }
}