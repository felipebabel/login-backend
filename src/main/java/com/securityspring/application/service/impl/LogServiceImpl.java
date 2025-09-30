package com.securityspring.application.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.domain.model.UserEntity;
import com.securityspring.domain.port.LogRepository;
import com.securityspring.domain.port.UserRepository;
import com.securityspring.infrastructure.adapters.dto.LogDto;
import com.securityspring.infrastructure.config.ProjectProperties;
import com.securityspring.domain.model.LogsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class LogServiceImpl implements LogServiceApi {

    @Getter
    private final ProjectProperties projectProperties;

    private final LogRepository logRepository;

    private final UserRepository userRepository;

    static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    public LogServiceImpl(final ProjectProperties projectProperties,
                          final LogRepository logRepository,
                          final UserRepository userRepository) {
        this.projectProperties = projectProperties;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<LogDto> getLogs(int page,
                                int size,
                                String sortBy,
                                String direction,
                                Long userIdentifier,
                                String action,
                                String username) {
        Sort sort;
        if ("username".equals(sortBy)) {
            sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by("user.username").descending()
                    : Sort.by("user.username").ascending();
        } else if ("userIdentifier".equals(sortBy)) {
            sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by("user.identifier").descending()
                    : Sort.by("user.identifier").ascending();
        } else {
            sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LogsEntity> logs = logRepository.findLogs(userIdentifier, username, action, pageable);

        return logs.map(l -> new LogDto(
                l.getIdentifier(),
                l.getAction(),
                l.getDescription(),
                l.getIpAddress(),
                l.getDate(),
                l.getUser() != null ? l.getUser().getIdentifier() : null,
                l.getUser() != null ? l.getUser().getUsername() : null
        ));
    }

    @Override
    public void setLog(final String action, final Long user)  {
        Optional<UserEntity> userEntity = userRepository.findByIdentifier(user);
        LogsEntity logsEntity = new LogsEntity();
        logsEntity.setAction(action);
        logsEntity.setDate(LocalDateTime.now());
        logsEntity.setDescription("");
        logsEntity.setIpAddress("");
        userEntity.ifPresent(logsEntity::setUser);
        this.logRepository.save(logsEntity);
    }

    @Override
    public void setLog(final String action, final Long user,
                       final HttpServletRequest httpServletRequest)  {
        Optional<UserEntity> userEntity = userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            LogsEntity logsEntity = new LogsEntity();
            logsEntity.setAction(action);
            logsEntity.setDate(LocalDateTime.now());
            logsEntity.setDescription("");
            logsEntity.setIpAddress(httpServletRequest.getRemoteAddr());
            logsEntity.setDeviceName(httpServletRequest.getHeader("User-Agent"));
            logsEntity.setUser(userEntity.get());
            this.logRepository.save(logsEntity);
            return;
        }
        LOGGER.info("Not found user: {}", user);

    }

    @Override
    public void setLog(final String action, final Long user, final String description)  {
        Optional<UserEntity> userEntity = userRepository.findByIdentifier(user);
        if (userEntity.isPresent()) {
            LogsEntity logsEntity = new LogsEntity();
            logsEntity.setAction(action);
            logsEntity.setDate(LocalDateTime.now());
            logsEntity.setDescription(description);
            logsEntity.setIpAddress("");
            logsEntity.setUser(userEntity.get());
            this.logRepository.save(logsEntity);
            return;
        }
        LOGGER.info("Not found user: {}", user);

    }

    @Override
    public void setLog(final String action, final String description)  {
        LogsEntity logsEntity = new LogsEntity();
        logsEntity.setAction(action);
        logsEntity.setDate(LocalDateTime.now());
        logsEntity.setDescription(description);
        logsEntity.setIpAddress("");
        this.logRepository.save(logsEntity);
    }

    @Override
    public int deleteOldLogs() {
        return this.logRepository.deleteOldLogs(LocalDateTime.now().minusDays(30));
    }
}
