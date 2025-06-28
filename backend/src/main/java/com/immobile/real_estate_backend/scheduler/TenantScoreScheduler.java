package com.immobile.real_estate_backend.scheduler;

import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.TenantScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantScoreScheduler {

    private final TenantScoreService tenantScoreService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 * * * * *")
    public void updateAllTenantScores() {
        log.info("Starting scheduled tenant score updates...");

        List<User> tenants = userRepository.findAllByRole("TENANT");
        log.info("Found {} tenants to update scores for", tenants.size());

        int updatedCount = 0;
        for (User tenant : tenants) {
            try {
                tenantScoreService.updateScoreForTenant(tenant.getUserId());
                updatedCount++;
            } catch (Exception e) {
                log.error("Failed to update score for tenant ID {}: {}", tenant.getUserId(), e.getMessage());
            }
        }

        log.info("Completed tenant score updates. Successfully updated {} out of {} tenants",
                updatedCount, tenants.size());
    }
}