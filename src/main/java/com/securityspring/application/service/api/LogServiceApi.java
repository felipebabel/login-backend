package com.securityspring.application.service.api;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.securityspring.infrastructure.adapters.dto.IpAccessDTO;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.adapters.dto.LoginAttemptsCountDTO;
import org.springframework.data.domain.Page;

public interface LogServiceApi {

    List<LoginAttemptsCountDTO> getLoginAttempts();

    List<IpAccessDTO> getAccessesByCountry();

    void setLog(final String action, final Long username, final String description);

    void setLog(final String action, final Long username, final String description,
                final HttpServletRequest httpServletRequest);

    void setLog(final String action, final Long username,
                final HttpServletRequest httpServletRequest);

    void setLog(final String action, final String description, final HttpServletRequest httpServletRequest);
    Page<LogDto> getLogs(final int page,
                         final int size,
                         final String sortBy,
                         final String direction,
                         final Long userIdentifier,
                         final String action,
                         final String username);

    int deleteOldLogs();
}