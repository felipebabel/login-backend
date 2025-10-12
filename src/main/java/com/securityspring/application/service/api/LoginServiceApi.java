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
import com.securityspring.infrastructure.adapters.vo.UserVO;
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

    UserVO inactiveAccount(final Long user,
                               final HttpServletRequest httpServletRequest);

    UserVO forcePasswordChange(final Long user,
                                   final HttpServletRequest httpServletRequest);

    UserVO getUserByUsername(final String username);

    UserVO getUserByName(final String name);

    Optional<UserEntity> findUser(final String user);

    UserVO login(final LoginRequestDto userEntity,
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

    Page<UserVO> getPendingUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserVO> getActiveSessions(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserVO> getBlockedUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    UserVO updateUserRole(final Long userIdentifier,
                              final RolesUserEnum role,
                              final Long userRequested,
                              final HttpServletRequest httpServletRequest);

    Page<UserVO> getInactiveUsers(final int page,
                                     final int size,
                                     final String sortBy,
                                     final String direction);

    Page<UserVO> getUsers(final int page,
                                      final int size,
                                      final String sortBy,
                                      final String direction,
                                      final Long userIdentifier,
                                      final String username,
                                      final String name);

    TotalAccountVO getTotalAccount();

    UserVO activeUser(final Long user,

                          final HttpServletRequest httpServletRequest);

    UserVO blockUser(final Long user,
                         final HttpServletRequest httpServletRequest);

    void deleteUser(final Long user,
                    final HttpServletRequest httpServletRequest);


    Page<UserVO> getActiveUsers(final int page,
                                    final int size,
                                    final String sortBy,
                                    final String direction);

    int deleteOldAccounts();

    void updateLastAccess(final String username);

        int deactivateUsers();

    void createDefaultUsers();

    void resetPassword(final String newPassword,
                        final String email,
                       final String user,
                       final HttpServletRequest httpServletRequest);

    UserVO updateUserStatus(final StatusEnum statusEnum,
                       final UserVO user);

}
