package com.securityspring.application.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import com.securityspring.application.service.api.EmailServiceApi;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.application.service.api.PasswordServiceApi;
import com.securityspring.domain.enums.GenderEnum;
import com.securityspring.domain.enums.LanguagesEnum;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.domain.exception.InvalidPasswordException;
import com.securityspring.domain.exception.PasswordExpiredException;
import com.securityspring.domain.exception.SamePasswordException;
import com.securityspring.domain.exception.UserAlreadyExistsException;
import com.securityspring.domain.exception.UserBlockedException;
import com.securityspring.domain.exception.UserInactiveException;
import com.securityspring.domain.exception.UserNotFoundException;
import com.securityspring.domain.exception.UserPendingException;
import com.securityspring.domain.model.ConfigEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.ConfigRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
import com.securityspring.infrastructure.adapters.dto.TotalAccountProjection;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.mapper.UserMapper;
import com.securityspring.infrastructure.adapters.vo.TotalAccountVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import com.securityspring.infrastructure.config.ProjectProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class LoginServiceImpl implements LoginServiceApi {

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String ANALYST = "analyst";

    @Getter
    private final ProjectProperties projectProperties;

    private final PasswordServiceApi passwordService;

    private final UserRepository userRepository;

    private final LogServiceApi logService;

    private final ConfigRepository configRepository;

    private final EmailServiceApi emailService;

    static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);


    public LoginServiceImpl(final ProjectProperties projectProperties,
                            final PasswordServiceApi passwordService,
                            final UserRepository userRepository,
                            final LogServiceApi logService,
                            final ConfigRepository configRepository,
                            final EmailServiceApi emailService) {
        this.projectProperties = projectProperties;
        this.passwordService = passwordService;
        this.userRepository = userRepository;
        this.logService = logService;
        this.configRepository = configRepository;
        this.emailService = emailService;
    }

    @Override
    public UserEntity updateUser(final UserEntity user,
                                 final UpdateAccountRequestDto account) {
        user.setUsername(account.getUser());
        user.setName(account.getName());
        user.setLastAccessDate(LocalDateTime.now());
        user.setLanguage(LanguagesEnum.fromString(account.getLanguage()));
        user.setAddress(account.getAddress());
        user.setPhone(account.getPhone());
        user.setState(account.getState());
        user.setZipCode(account.getZipCode());
        user.setCountry(account.getCountry());
        user.setAddressComplement(account.getAddressComplement());
        user.setBirthDate(account.getBirthDate());
        user.setGender(GenderEnum.fromString(account.getGender()));
        user.setCity(account.getCity());
        this.userRepository.save(user);
        LOGGER.info("User updated: Id: {}", user.getIdentifier());
        return user;
    }

    @Override
    public UserEntity saveUser(final String password,
                               final String user,
                               final String email,
                               final String firstName,
                               final StatusEnum statusEnum,
                               final RolesUserEnum role,
                               final LanguagesEnum language) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user);
        userEntity.setPassword(password);
        userEntity.setName(firstName);
        userEntity.setEmail(email);
        userEntity.setLanguage(language);
        userEntity.setStatus(statusEnum);
        userEntity.setCreationUserDate(LocalDateTime.now());
        userEntity.setLoginAttempt(0);
        userEntity.setRole(role);
        if (user.equalsIgnoreCase("user")) {
            userEntity.setPasswordChangeDate(LocalDateTime.now().minusDays(89));
        }
        this.userRepository.save(userEntity);
        LOGGER.info("User saved: Id: {}", userEntity.getIdentifier());
        return userEntity;
    }

    @Override
    public UserVO inactiveAccount(final Long user,
                                      final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            userEntity.get().setStatus(StatusEnum.INACTIVE);
            this.userRepository.save(userEntity.get());
            LOGGER.info("User inactivated: Id: {}", userEntity.get().getIdentifier());
            this.logService.setLog("INACTIVATE ACCOUNT", user, httpServletRequest);
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found to inactive: " + user);
        }
    }

    @Override
    public UserVO forcePasswordChange(final Long user,
                                          final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            userEntity.get().setForcePasswordChange(true);
            this.userRepository.save(userEntity.get());
            LOGGER.info("User forced to change password: Id: {}", user);
            this.logService.setLog("FORCED CHANGE PASSWORD", user, "User forced to change password.", httpServletRequest);
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found to force change password: " + user);
        }
    }

    @Override
    public UserVO getUserByUsername(final String username) {
        final Optional<UserEntity> userEntity = this.userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found. Username: " + username);
        }
    }

    @Override
    public UserVO getUserByName(final String name) {
        final Optional<UserEntity> userEntity = this.userRepository.findByUsername(name);
        if (userEntity.isPresent()) {
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found. Name: " + name);
        }
    }

    @Override
    public Optional<UserEntity> findUser(final String user) {
        return this.userRepository.findByUsername(user.trim());
    }

    @Override
    public UserVO login(final LoginRequestDto loginRequestDto,
                        final HttpServletRequest httpServletRequest) {
        String username = loginRequestDto.getUser().trim();
        UserEntity user = findUser(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        switch (user.getStatus()) {
            case PENDING -> {
                logService.setLog(
                        "LOGIN ATTEMPT FAILED",
                        user.getIdentifier(),
                        String.format("Login attempt with status PENDING for user: %s", username)
                );
                throw new UserPendingException("Your account is pending to active.");
            }
            case BLOCKED -> {
                logService.setLog(
                        "LOGIN ATTEMPT FAILED",
                        user.getIdentifier(),
                        String.format("Login attempt with status BLOCKED for user: %s", username)
                );
                throw new UserBlockedException("Your account is blocked.");
            }
            case INACTIVE -> {
                logService.setLog(
                        "LOGIN ATTEMPT FAILED",
                        user.getIdentifier(),
                        String.format("Login attempt with status INACTIVE for user: %s", username)
                );
                throw new UserInactiveException("Your account is inactive.");
            }
        }
        if (user.isForcePasswordChange()) {
            throw new PasswordExpiredException("Password expired");
        }
        Optional<ConfigEntity> config = this.configRepository.findByKey("passwordExpiryDays");
        long value = 90L;
        if (config.isPresent()) {
            value = Long.parseLong(config.get().getValue());
        }
        final LocalDate expiryDate = LocalDate.now().minusDays(value);
        if (user.getPasswordChangeDate().toLocalDate().isBefore(expiryDate)) {
            throw new PasswordExpiredException("Password expired");
        }
        if (!passwordService.checkPassword(loginRequestDto.getPassword(), user.getPassword())) {
            handleInvalidPassword(user, httpServletRequest);
        }

        updateLoginAttempt(user, 0, "LOGIN ATTEMPT OK");
        updateLogin(user, 0);
        this.logService.setLog("LOGIN ACCOUNT", user.getIdentifier(), httpServletRequest);
        return UserMapper.toVO(user);
    }

    private void handleInvalidPassword(final UserEntity user,
                                       final HttpServletRequest httpServletRequest) {
        int attempts = user.getLoginAttempt() + 1;
        if (attempts >= 5) {
            blockUser(user.getIdentifier(), httpServletRequest);
            throw new InvalidPasswordException("User blocked due to too many failed login attempts");
        }
        updateLoginAttempt(user, attempts, "LOGIN ATTEMPT FAILED");
        throw new InvalidPasswordException("Invalid password");
    }

    @Override
    public void createAccount(final CreateAccountRequestDto createAccount,
                              final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        final Optional<UserEntity> optionalUser = this.findUser(createAccount.getUser());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists: User" + optionalUser.get().getUsername());
        }
        final String encryptedPassword = this.passwordService.encryptPassword(createAccount.getPassword());
        final UserEntity user = this.saveUser(encryptedPassword, createAccount.getUser(), createAccount.getEmail(),
                createAccount.getName(), StatusEnum.PENDING, RolesUserEnum.USER, LanguagesEnum.fromString(createAccount.getLanguage()));
        this.logService.setLog("CREATED ACCOUNT", user.getIdentifier(), httpServletRequest);
        this.emailService.sendEmail(user, httpServletRequest);
        LOGGER.info("Account created successfully");
    }

    @Override
    public List<NewUsersPerMonthDTO> getNewAccountMonth() {
        List<Object[]> results = userRepository.getNewAccountMonth();
        return results.stream()
                .map(r -> new NewUsersPerMonthDTO(
                        ((Number) r[0]).intValue(),
                        ((Number) r[1]).intValue(),
                        ((Number) r[2]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateAccount(final UpdateAccountRequestDto account,
                              final Long userIdentifier,
                              final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        UserEntity user = this.userRepository.findByIdentifier(userIdentifier)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userIdentifier));
        if (!user.getUsername().equalsIgnoreCase(account.getUser())) {
            Optional<UserEntity> optionalUser = this.userRepository.findByUsername(account.getUser());
            if (optionalUser.isPresent()) {
                throw new UserAlreadyExistsException("User already exists: User" + optionalUser.get().getUsername());
            }
        }
        final UserEntity userUpdated = this.updateUser(user, account);
        this.logService.setLog("UPDATE ACCOUNT", userUpdated.getIdentifier(), httpServletRequest);
        LOGGER.info("Account updated successfully");
    }

    @Override
    public void logout(final Long user,
                       final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> optionalUser = this.userRepository.findByIdentifier(user);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found: User" + user);
        }
        optionalUser.get().setLoginDate(null);
        this.userRepository.save(optionalUser.get());
        this.logService.setLog("USER LOGOUT", user, httpServletRequest);
    }


    @Override
    public void updateLoginAttempt(final UserEntity userEntity,
                                   final int loginAttempt,
                                   final String action) {
        userEntity.setLoginAttempt(loginAttempt);
        this.userRepository.save(userEntity);
        this.logService.setLog(action, userEntity.getIdentifier(), "Login attempt: " + loginAttempt);
        LOGGER.info("User {} failed login.", userEntity.getIdentifier());
    }

    @Override
    public void updateLogin(final UserEntity userEntity,
                            final int loginAttempt) {
        userEntity.setLoginAttempt(loginAttempt);
        userEntity.setLoginDate(LocalDateTime.now());
        this.userRepository.save(userEntity);
        this.logService.setLog("UPDATED LOGIN INFORMATION", userEntity.getIdentifier(), String.format("Updated login date: %s. "
                + "And login attempt: %d", userEntity.getLoginDate(), loginAttempt));
        LOGGER.info("User {} login.", userEntity.getIdentifier());
    }

    @Override
    public UserVO activeUser(final Long user,
                                 final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            userEntity.get().setStatus(StatusEnum.ACTIVE);
            this.userRepository.save(userEntity.get());
            LOGGER.info("User activated: Id: {}", userEntity.get().getIdentifier());
            this.logService.setLog("ACTIVATED ACCOUNT", user, httpServletRequest);
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found to active: " + user);
        }
    }

    @Override
    public UserVO blockUser(final Long user,
                                final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            userEntity.get().setStatus(StatusEnum.BLOCKED);
            this.userRepository.save(userEntity.get());
            LOGGER.info("User blocked: Id: {}", userEntity.get().getIdentifier());
            this.logService.setLog("BLOCKED ACCOUNT", user, httpServletRequest);
            return UserMapper.toVO(userEntity.get());
        } else {
            throw new UserNotFoundException("User not found to block: " + user);
        }
    }


    @Override
    public void deleteUser(final Long user, final HttpServletRequest httpServletRequest) {
        final Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            this.userRepository.delete(userEntity.get());
            LOGGER.info("User deleted: Id: {}", user);
            this.logService.setLog("DELETED ACCOUNT", user, httpServletRequest);
        } else {
            throw new UserNotFoundException("User not found to delete: " + user);
        }
    }

    @Override
    public TotalAccountVO getTotalAccount() {
        TotalAccountProjection projection = this.userRepository.getTotalAccount();
        return new TotalAccountVO(
                projection.getTotal(),
                projection.getTotalActive(),
                projection.getTotalInactive(),
                projection.getTotalPending(),
                projection.getTotalBlocked(),
                projection.getTotalActiveSession()
        );
    }


    @Override
    public Page<UserVO> getPendingUsers(final int page,
                                            final int size,
                                            final String sortBy,
                                            final String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserEntity> userList = this.userRepository.findByStatus(StatusEnum.PENDING, pageable);
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public Page<UserVO> getActiveSessions(final int page,
                                              final int size,
                                              final String sortBy,
                                              final String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = this.userRepository.findByActiveSession(StatusEnum.ACTIVE, pageable, LocalDateTime.now().minusMinutes(10));
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public Page<UserVO> getBlockedUsers(final int page,
                                            final int size,
                                            final String sortBy,
                                            final String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = this.userRepository.findByStatus(StatusEnum.BLOCKED, pageable);
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public UserVO updateUserRole(final Long userIdentifier,
                                     final RolesUserEnum role,
                                     final Long userRequested,
                                     final HttpServletRequest httpServletRequest) {
        Optional<UserEntity> user = this.userRepository.findByIdentifier(userIdentifier);
        if (user.isPresent()) {
            user.get().setRole(role);
            user.get().setUpdateDate(LocalDateTime.now());
            userRepository.save(user.get());
            LOGGER.info("Updated role: {} for user: {} by user: {} ", role.getDescription(), userIdentifier, userRequested);
            this.logService.setLog("UPDATED ROLE", userIdentifier, String.format(
                    "Updated role %s for user: %d by user: %d", role.getDescription(), userIdentifier, userRequested), httpServletRequest);
            return UserMapper.toVO(user.get());
        } else {
            throw new UserNotFoundException("User not found. User: " + userIdentifier);
        }
    }

    @Override
    public Page<UserVO> getInactiveUsers(final int page,
                                             final int size,
                                             final String sortBy,
                                             final String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = this.userRepository.findByStatus(StatusEnum.INACTIVE, pageable);
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public Page<UserVO> getUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction,
                                     final Long userIdentifier,
                                     final String username,
                                     final String name) {
        Sort sort;
        if ("username".equals(sortBy)) {
            sort = direction.equalsIgnoreCase("desc")
                    ? JpaSort.unsafe("u.username DESC")
                    : JpaSort.unsafe("u.username ASC");
        } else {
            sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = this.userRepository.findByUsernameOrIdentifier(userIdentifier, username, name, pageable);
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public Page<UserVO> getActiveUsers(final int page,
                                           final int size,
                                           final String sortBy,
                                           final String direction) {
        final Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        final Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> userList = this.userRepository.findByStatus(StatusEnum.ACTIVE, pageable);
        return new PageImpl<>(
                userList.getContent().stream()
                        .map(UserMapper::toVO)
                        .toList(),
                pageable,
                userList.getTotalElements()
        );
    }

    @Override
    public int deleteOldAccounts() {
        return this.userRepository.deleteOldAccounts(LocalDateTime.now().minusDays(60));
    }

    @Override
    public void updateLastAccess(final String username) {
        this.userRepository.updateLastAccess(username);
    }

    @Override
    public int deactivateUsers() {
        return this.userRepository.deactivateUsers(LocalDateTime.now().minusDays(30), StatusEnum.INACTIVE, StatusEnum.ACTIVE);
    }

    @Override
    public void createDefaultUsers() {
        final Optional<UserEntity> user = this.userRepository.findByUsername(USER);
        if (user.isEmpty()) {
            LOGGER.info("Creating user user");
            final String passwordEncrypted = this.passwordService.encryptPassword(USER);
            final UserEntity userEntity = this.saveUser(passwordEncrypted, USER, "dsadas@gmail.com", "User", StatusEnum.ACTIVE,
                    RolesUserEnum.USER, LanguagesEnum.EN);
            LOGGER.info("User user created");
            this.logService.setLog("CREATE ACCOUNT", userEntity.getIdentifier(), "User user created");
        } else {
            LOGGER.info("User user already exists");
        }
        final Optional<UserEntity> analyst = this.userRepository.findByUsername(ANALYST);
        if (analyst.isEmpty()) {
            LOGGER.info("Creating user analyst");
            final String passwordEncrypted = this.passwordService.encryptPassword(ANALYST);
            final UserEntity userEntity = this.saveUser(passwordEncrypted, ANALYST, "analyst@user.com", "Analyst", StatusEnum.ACTIVE,
                    RolesUserEnum.ANALYST, LanguagesEnum.EN);
            LOGGER.info("User analyst created");
            this.logService.setLog("CREATE ACCOUNT", userEntity.getIdentifier(), "User analyst created");
        } else {
            LOGGER.info("User analyst already exists");
        }
        final Optional<UserEntity> admin = this.userRepository.findByUsername(ADMIN);
        if (admin.isEmpty()) {
            LOGGER.info("Creating user admin");
            final String passwordEncrypted = this.passwordService.encryptPassword(ADMIN);
            final UserEntity userEntity = this.saveUser(passwordEncrypted, ADMIN, "admin2@user.com", "Administrator Admin", StatusEnum.ACTIVE,
                    RolesUserEnum.ADMIN, LanguagesEnum.EN);
            LOGGER.info("User admin created");
            this.logService.setLog("CREATE ACCOUNT", userEntity.getIdentifier(), "User admin2 created");
        } else {
            LOGGER.info("User admin already exists");
        }
    }

    @Override
    public void resetPassword(final String newPassword,
                              final String email,
                              final String user,
                              final HttpServletRequest httpServletRequest) {
        if (Objects.isNull(newPassword) || newPassword.length() < 8) {
            throw new BadRequestException("The new password must have at least 8 characters.");
        }
        UserEntity userEntity;
        if (Objects.nonNull(user)) {
            userEntity = this.userRepository.findByUsername(user)
                    .orElseThrow(() -> new UserNotFoundException("Not found user with username: " + user));
        } else {
            userEntity = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));
        }
        final String encryptedPassword = this.passwordService.encryptPassword(newPassword);
        if (this.passwordService.checkPassword(newPassword, userEntity.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the current password.");
        }
        userEntity.setUpdateDate(LocalDateTime.now());
        userEntity.setPassword(encryptedPassword);
        userEntity.setPasswordChangeDate(LocalDateTime.now());
        userEntity.setForcePasswordChange(false);
        this.userRepository.save(userEntity);
        this.logService.setLog("PASSWORD RESET", userEntity.getIdentifier(), "Password reset successfully.");
    }

    @Override
    public UserVO updateUserStatus(final StatusEnum statusEnum,
                                 final UserVO user) {
        Optional<UserEntity> userEntity = this.userRepository.findByIdentifier(user.getIdentifier());
        if (userEntity.isPresent()) {
            userEntity.get().setStatus(statusEnum);
            this.userRepository.save(userEntity.get());
            return UserMapper.toVO(userEntity.get());

        }
        return new UserVO();
    }

}
