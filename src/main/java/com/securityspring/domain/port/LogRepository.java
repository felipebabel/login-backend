package com.securityspring.domain.port;

import java.time.LocalDateTime;
import java.util.List;
import com.securityspring.domain.model.LogsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogRepository extends EntityRepository<LogsEntity, Long> {

    int deleteOldLogs(final LocalDateTime date);

    Page<LogsEntity> findLogs(
            final Long userIdentifier,
            final String username,
            final String action,
            final Pageable pageable
    );

    List<Object[]> getLoginAttempts();

}
