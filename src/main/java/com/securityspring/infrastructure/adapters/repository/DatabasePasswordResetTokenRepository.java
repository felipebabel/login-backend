package com.securityspring.infrastructure.adapters.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.transaction.Transactional;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.PasswordResetTokenEntity;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.PasswordResetTokenRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabasePasswordResetTokenRepository extends PasswordResetTokenRepository {

    @Query("SELECT t FROM PasswordResetTokenEntity t " +
            "WHERE t.user = :user " +
            "AND t.code = :code " +
            "AND t.status = :status " +
            "AND t.expirationDate > :date")
    Optional<PasswordResetTokenEntity> findValidToken(
            @Param("user") UserEntity user,
            @Param("code") String code,
            @Param("status") StatusEnum status,
            @Param("date") LocalDateTime date
    );

    @Modifying
    @Override
    @Transactional
    @Query("DELETE FROM PasswordResetTokenEntity u "
            + " WHERE u.expirationDate < :date")
    int deleteOldTokens(@Param("date") final LocalDateTime date);
}
