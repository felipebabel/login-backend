package com.securityspring.domain.port;

import java.time.LocalDateTime;
import java.util.Optional;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.PasswordResetTokenEntity;
import com.securityspring.domain.model.UserEntity;

public interface PasswordResetTokenRepository extends EntityRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findValidToken(
            UserEntity user,
            String code,
            StatusEnum status,
            LocalDateTime now
    );
}
