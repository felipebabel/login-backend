package com.securityspring.infrastructure.adapters.repository;

import java.time.LocalDateTime;
import jakarta.transaction.Transactional;
import com.securityspring.domain.model.LogsEntity;
import com.securityspring.domain.port.LogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseLogRepository extends LogRepository {

    @Modifying
    @Transactional
    @Override
    @Query(value = "DELETE FROM LogsEntity WHERE date < :dateDelete")
    int deleteOldLogs(@Param("dateDelete") LocalDateTime date);

    @Override
    @Query("SELECT l FROM LogsEntity l " +
            "LEFT JOIN l.user u " +
            "WHERE (:userIdentifier IS NULL OR u.identifier = :userIdentifier) " +
            "AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
            "AND (:action IS NULL OR LOWER(l.action) LIKE LOWER(CONCAT('%', :action, '%')))")
    Page<LogsEntity> findLogs(@Param("userIdentifier") Long userIdentifier,
                              @Param("username") String username,
                              @Param("action") String action,
                              Pageable pageable);
}

