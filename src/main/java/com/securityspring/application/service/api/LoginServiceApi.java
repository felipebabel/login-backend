package com.securityspring.application.service.api;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.TotalAccountVO;
import org.springframework.data.domain.Page;

public interface LoginServiceApi {

    UserEntity saveUser(final String password,
                        final String user,
                        final String email,
                        final String firstName,
                        final StatusEnum statusEnum,
                        final RolesUserEnum role);

    UserEntity updateUser(final UserEntity user,
                                 final UpdateAccountRequestDto account);

    UserEntity inactiveAccount(final Long user,
                               final HttpServletRequest httpServletRequest);

    UserEntity forcePasswordChange(final Long user);

    UserEntity getUserByUsername(final String username);

    UserEntity getUserByName(final String name);

    Optional<UserEntity> findUser(final String user);

    UserEntity login(final LoginRequestDto userEntity,
                    final HttpServletRequest httpServletRequest);

    void createAccount(final CreateAccountRequestDto createAccount,
                       final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException;

    void updateAccount(final UpdateAccountRequestDto createAccount,
                       final Long userIdentifier,
                       final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException;

    void logout(final Long user,
                final HttpServletRequest httpServletRequest);

    void updateLoginAttempt(final UserEntity userEntity,
                            final int loginAttempt);

    void updateLogin(final UserEntity userEntity,
                            final int loginAttempt);

    Page<UserEntity> getPendingUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserEntity> getActiveSessions(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserEntity> getBlockedUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    UserEntity updateUserRole(final Long userIdentifier,
                              final RolesUserEnum role,
                              final Long userRequested,
                              final HttpServletRequest httpServletRequest);

    Page<UserEntity> getInactiveUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserEntity> getUsers(final int page,
                                      final int size,
                                      final String sortBy,
                                      final String direction,
                                      final Long userIdentifier,
                                      final String username,
                                      final String name);

    TotalAccountVO getTotalAccount();

    UserEntity activeUser(final Long user);

    UserEntity blockUser(final Long user);

    void deleteUser(final Long user,
                    final HttpServletRequest httpServletRequest);


    Page<UserEntity> getActiveUsers(final int page,
                                    final int size,
                                    final String sortBy,
                                    final String direction);

    int deleteOldAccounts();

    int deactivateUsers();

    void createDefaultUsers();

    void resetPassword(final String newPassword,
                        final String email,
                       final String user,
                       final HttpServletRequest httpServletRequest);

    void updateUserStatus(final StatusEnum statusEnum,
                       final UserEntity user);

}
