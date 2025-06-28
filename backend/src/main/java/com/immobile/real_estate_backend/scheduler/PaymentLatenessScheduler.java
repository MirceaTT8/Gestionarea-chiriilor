package com.immobile.real_estate_backend.scheduler;

import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Payment;
import com.immobile.real_estate_backend.repository.PaymentRepository;
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
public class PaymentLatenessScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void evaluatePaymentLateness() {
        log.info("Running daily payment lateness evaluation...");

        List<Payment> payments = paymentRepository.findAllWithLease();

        int updatedCount = 0;

        for (Payment payment : payments) {
            if (payment.getWasLate() != null) continue;

            Lease lease = payment.getLease();
            if (lease == null || lease.getStartDate() == null) continue;

            int dueDay = lease.getStartDate().getDayOfMonth();
            LocalDate paymentDate = payment.getPaymentDate();
            LocalDate dueDate = LocalDate.of(
                    paymentDate.getYear(),
                    paymentDate.getMonth(),
                    Math.min(dueDay, paymentDate.lengthOfMonth())
            );

            boolean late = paymentDate.isAfter(dueDate);
            payment.setWasLate(late);
            updatedCount++;
        }

        if (updatedCount > 0) {
            paymentRepository.saveAll(payments);
            log.info("Updated {} payments with lateness status.", updatedCount);
        } else {
            log.info("No new payments required lateness evaluation.");
        }
    }
}
