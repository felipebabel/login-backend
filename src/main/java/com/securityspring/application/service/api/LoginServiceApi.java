package com.securityspring.application.service.api;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.domain.enums.LanguagesEnum;
import com.securityspring.domain.enums.RolesUserEnum;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.adapters.vo.TotalAccountVO;
import org.springframework.data.domain.Page;

public interface LoginServiceApi {

    UserEntity saveUser(final String password,
                        final String user,
                        final String email,
                        final String firstName,
                        final StatusEnum statusEnum,
                        final RolesUserEnum role,
                        final LanguagesEnum languagesEnum);

    UserEntity updateUser(final UserEntity user,
                                 final UpdateAccountRequestDto account);

    UserEntity inactiveAccount(final Long user,
                               final HttpServletRequest httpServletRequest);

    UserEntity forcePasswordChange(final Long user,
                                   final HttpServletRequest httpServletRequest);

    UserEntity getUserByUsername(final String username);

    UserEntity getUserByName(final String name);

    Optional<UserEntity> findUser(final String user);

    UserEntity login(final LoginRequestDto userEntity,
                    final HttpServletRequest httpServletRequest);

    List<NewUsersPerMonthDTO> getNewAccountMonth();

    void createAccount(final CreateAccountRequestDto createAccount,
                       final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException;

    void updateAccount(final UpdateAccountRequestDto createAccount,
                       final Long userIdentifier,
                       final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException;

    void logout(final Long user,
                final HttpServletRequest httpServletRequest);

    void updateLoginAttempt(final UserEntity userEntity,
                            final int loginAttempt,
                            final String action);

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

    UserEntity activeUser(final Long user,

                          final HttpServletRequest httpServletRequest);

    UserEntity blockUser(final Long user,
                         final HttpServletRequest httpServletRequest);

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
