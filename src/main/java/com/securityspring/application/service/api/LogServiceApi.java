package com.securityspring.application.service.api;

import com.securityspring.domain.model.LogsEntity;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import org.springframework.data.domain.Page;

public interface LogServiceApi {

    void setLog(final String action, final Long username);

    void setLog(final String action, final Long username, final String description);

    void setLog(final String action, final String description);
    Page<LogDto> getLogs(final int page,
                         final int size,
                         final String sortBy,
                         final String direction,
                         final Long userIdentifier,
                         final String action,
                         final String username);

    int deleteOldLogs();
}