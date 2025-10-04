package com.securityspring.infrastructure.adapters.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;
import com.securityspring.domain.enums.StatusEnum;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.dto.IpAccessDTO;
import com.securityspring.infrastructure.adapters.dto.NewUsersPerMonthDTO;
import com.securityspring.infrastructure.adapters.dto.TotalAccountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseUserRepository extends UserRepository {

    @Override
    @Query("select u FROM UserEntity u "
            + " WHERE u.username = :username")
    Optional<UserEntity> findByUsername(@Param("username") final String username);

    @Override
    @Query("select u FROM UserEntity u "
            + " WHERE u.identifier = :identifier")
    Optional<UserEntity> findByIdentifier(@Param("identifier") final Long identifier);

    @Override
    @Query("select u FROM UserEntity u "
            + " WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") final String email);


    @Override
    @Query(value = "SELECT " +
            " COUNT(*) AS total, " +
            " SUM(CASE WHEN DS_STATUS = 'ACTIVE' THEN 1 ELSE 0 END) AS totalActive, " +
            " SUM(CASE WHEN DS_STATUS = 'INACTIVE' THEN 1 ELSE 0 END) AS totalInactive, " +
            " SUM(CASE WHEN DS_STATUS = 'PENDING' THEN 1 ELSE 0 END) AS totalPending, " +
            " SUM(CASE WHEN DS_STATUS = 'BLOCKED' THEN 1 ELSE 0 END) AS totalBlocked, " +
            " SUM(CASE WHEN DT_LOGIN IS NOT NULL THEN 1 ELSE 0 END) AS totalActiveSession " +
            " FROM login_user " +
            " WHERE DS_USERNAME <> 'admin'", nativeQuery = true)
    TotalAccountProjection getTotalAccount();

    @Override
    @Query("SELECT u FROM UserEntity u WHERE u.status = :status AND u.role <> 'ADMIN'")
    Page<UserEntity> findByStatus(@Param("status") final StatusEnum status,
                                  final Pageable pageable);

    @Modifying
    @Override
    @Transactional
    @Query("DELETE FROM UserEntity u "
            + " WHERE u.lastAccessDate < :date"
            + " AND u.username <> 'admin'")
    int deleteOldAccounts(@Param("date") final LocalDateTime date);

    @Override
    @Query("select u FROM UserEntity u "
            + " WHERE u.status = :status"
            + " AND u.loginDate is not null"
            + " AND u.username <> 'admin'")
    Page<UserEntity> findByActiveSession(@Param("status") final StatusEnum status,
                                         final Pageable pageable);


    @Modifying
    @Override
    @Transactional
    @Query("UPDATE UserEntity u "
            + " SET u.status = :inactive "
            + " WHERE u.lastAccessDate < :date "
            + " AND u.status = :active "
            + " AND u.username <> 'admin'")
    int deactivateUsers(@Param("date") final LocalDateTime date,
                        @Param("inactive") final StatusEnum inactive,
                        @Param("active") final StatusEnum active);

    @Override
    @Query("SELECT u FROM UserEntity u " +
            "WHERE (:userIdentifier IS NULL OR u.identifier = :userIdentifier) " +
            "AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
            "AND (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND username <> 'admin'"
    )
    Page<UserEntity> findByUsernameOrIdentifier(@Param("userIdentifier") final Long userIdentifier,
                                                @Param("username") final String username,
                                                @Param("name") final String name,
                                                final Pageable pageable);

    @Query(value = "SELECT EXTRACT(YEAR FROM DT_CREATION_USER) as year, " +
            "EXTRACT(MONTH FROM DT_CREATION_USER) as month, " +
            "COUNT(cd_identifier) as totalUsers " +
            "FROM login_user " +
            "GROUP BY EXTRACT(YEAR FROM DT_CREATION_USER), EXTRACT(MONTH FROM DT_CREATION_USER) " +
            "ORDER BY EXTRACT(YEAR FROM DT_CREATION_USER), EXTRACT(MONTH FROM DT_CREATION_USER)",
            nativeQuery = true)
    List<Object[]> getNewAccountMonth();


}

