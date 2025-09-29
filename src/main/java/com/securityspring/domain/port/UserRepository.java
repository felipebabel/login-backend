package com.securityspring.domain.port;

import java.time.LocalDateTime;
import java.util.Optional;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.infrastructure.adapters.dto.TotalAccountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends EntityRepository<UserEntity, Long> {

    Optional<UserEntity> findByIdentifier(final Long identifier);

    Optional<UserEntity> findByUsername(final String username);

    Optional<UserEntity> findByEmail(final String email);

    TotalAccountProjection getTotalAccount();

    Page<UserEntity> findByStatus(final StatusEnum status,
                                  final Pageable pageable);

    Page<UserEntity> findByActiveSession(final StatusEnum status,
                                         final Pageable pageable);

    int deleteOldAccounts(final LocalDateTime dateTime);

    int deactivateUsers(final LocalDateTime dateTime,
                        final StatusEnum inactive,
                        final StatusEnum active);

    Page<UserEntity> findByUsernameOrIdentifier(final Long userIdentifier,
                                                final String username,
                                                final String name,
                                                final Pageable pageable);
}
