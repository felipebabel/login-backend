package com.securityspring.infrastructure.adapters.repository;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.transaction.Transactional;
import com.securityspring.domain.model.LogsEntity;
import com.securityspring.domain.port.LogRepository;
import com.securityspring.infrastructure.adapters.dto.IpAccessDTO;
import com.securityspring.infrastructure.adapters.dto.LoginAttemptsCountDTO;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
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
            "AND (:action IS NULL OR LOWER(l.action) LIKE LOWER(CONCAT('%', :action, '%'))) " +
            "AND u.username <> 'admin'")
    Page<LogsEntity> findLogs(@Param("userIdentifier") Long userIdentifier,
                              @Param("username") String username,
                              @Param("action") String action,
                              Pageable pageable);

    @Query("SELECT l.action, COUNT(l) " +
            "FROM LogsEntity l " +
            "WHERE l.action IN ('LOGIN ACCOUNT', 'LOGIN_FAILED') " +
            "GROUP BY l.action")
    List<Object[]> getLoginAttempts();

    @Query("SELECT new com.securityspring.infrastructure.adapters.dto.IpAccessDTO(l.ipAddress, COUNT(l)) " +
            "FROM LogsEntity l " +
            "WHERE l.action LIKE '%LOGIN ATTEMPT%' " +
            "AND l.ipAddress is not null " +
            "AND l.ipAddress <> '' " +
            "GROUP BY l.ipAddress")
    List<IpAccessDTO> getAccessesByCountry();

}

