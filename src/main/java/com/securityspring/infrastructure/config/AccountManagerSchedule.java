package com.securityspring.infrastructure.config;

import java.time.LocalDateTime;
import com.securityspring.application.service.api.LogServiceApi;
import com.securityspring.application.service.api.LoginServiceApi;
import com.securityspring.domain.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountManagerSchedule {

    static final Logger LOGGER = LoggerFactory.getLogger(AccountManagerSchedule.class);

    private final LogServiceApi logService;

    private final LoginServiceApi loginService;

    @Autowired
    public AccountManagerSchedule(final LogServiceApi logService,
                                  final LoginServiceApi loginService) {
        this.logService = logService;
        this.loginService = loginService;
    }

    @Scheduled(cron = "0 0 6 * * ?", zone = "America/Sao_Paulo")
    public void runDailyDeletion() {
        LOGGER.info("Starting daily maintenance tasks at {}.", LocalDateTime.now());
        LOGGER.info("Starting daily scheduled task to delete logs older than 30 days.");
        UserEntity userEntity = this.loginService.getUserByUsername("admin");
        int deletedCount = this.logService.deleteOldLogs();
        this.logService.setLog("DAILY SCHEDULE", userEntity.getIdentifier(), String.format("Daily log deletion completed. Total logs deleted: %d",
                deletedCount));
        LOGGER.info("Daily log deletion completed. Total logs deleted: {}", deletedCount);
        LOGGER.info("Beginning account status update for users inactive for 30+ days.");
        int deactivatedUsersCount = this.loginService.deactivateUsers();
        this.logService.setLog("DAILY SCHEDULE", userEntity.getIdentifier(), String.format("Account status update completed. %d users were set to INACTIVE.",
                deactivatedUsersCount));
        LOGGER.info("Account status update completed. {} users were set to INACTIVE.", deactivatedUsersCount);
        LOGGER.info("Beginning deletion of accounts inactive for 60+ days.");
        int deletedOldAccountsCount = this.loginService.deleteOldAccounts();
        this.logService.setLog("DAILY SCHEDULE", userEntity.getIdentifier(), String.format("Account deletion completed. %d accounts were permanently deleted.",
                deletedOldAccountsCount));
        LOGGER.info("Account deletion completed. {} accounts were permanently deleted.", deletedOldAccountsCount);
        LOGGER.info("Daily maintenance tasks finished at {}.", LocalDateTime.now());
    }

}
