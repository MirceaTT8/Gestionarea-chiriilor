package com.immobile.real_estate_backend.scheduler;

import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import com.immobile.real_estate_backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaseExpirationScheduler {

    private final LeaseRepository leaseRepository;
    private final PropertyService propertyService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processExpiredLeases() {
        log.info("Starting daily lease expiration check...");

        try {
            LocalDate today = LocalDate.now();

            List<Lease> expiredLeases = leaseRepository.findActiveExpiredLeases(today);

            if (expiredLeases.isEmpty()) {
                log.info("No expired leases found for today: {}", today);
                return;
            }

            int processedCount = 0;
            int errorCount = 0;

            for (Lease lease : expiredLeases) {
                try {
                    lease.setStatus(LeaseStatus.TERMINATED);
                    leaseRepository.save(lease);

                    Property property = lease.getProperty();
                    if (property.getStatus() == PropertyStatus.RENTED) {
                        propertyService.updatePropertyStatus(property.getPropertyId(), PropertyStatus.AVAILABLE);

                        log.info("Processed expired lease ID: {} - Property '{}' (ID: {}) set to AVAILABLE",
                                lease.getLeaseId(),
                                property.getAddress(),
                                property.getPropertyId());
                        processedCount++;
                    } else {
                        log.warn("Property ID: {} was not in RENTED status, current status: {}",
                                property.getPropertyId(), property.getStatus());
                    }

                } catch (Exception e) {
                    errorCount++;
                    log.error("Failed to process expired lease ID: {} - Error: {}",
                            lease.getLeaseId(), e.getMessage(), e);
                }
            }

            log.info("Lease expiration processing completed. Processed: {}, Errors: {}",
                    processedCount, errorCount);

        } catch (Exception e) {
            log.error("Critical error in lease expiration scheduler: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void processExpiredLeasesManually() {
        log.info("Manual lease expiration check triggered");
        processExpiredLeases();
    }
}