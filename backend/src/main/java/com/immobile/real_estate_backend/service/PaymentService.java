package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.PaymentConverter;
import com.immobile.real_estate_backend.model.dto.PaymentDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.NotificationStatus;
import com.immobile.real_estate_backend.model.enums.NotificationType;
import com.immobile.real_estate_backend.model.enums.PaymentMethod;
import com.immobile.real_estate_backend.model.enums.PaymentStatus;
import com.immobile.real_estate_backend.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final PaymentConverter paymentConverter;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;

    @Value("${stripe.keys.secret}")
    private String apiKey;

    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentRequest) {
        Lease lease = leaseRepository.findById(paymentRequest.getLeaseId())
                .orElseThrow(() -> {
                    log.error("Lease not found with ID: {}", paymentRequest.getLeaseId());
                    return new RuntimeException("Lease not found");
                });

        LocalDate now = LocalDate.now();
        if (isRentPaidForCurrentBillingCycle(paymentRequest.getLeaseId())) {
            throw new RuntimeException("Rent for this billing cycle has already been paid");
        }

        if (paymentRequest.getAmount().compareTo(lease.getMonthlyRent()) != 0) {
            log.warn("Payment amount mismatch for lease {}: Expected {}, Actual {}",
                    lease.getLeaseId(), lease.getMonthlyRent(), paymentRequest.getAmount());
            throw new RuntimeException("Payment amount must exactly match the monthly rent");
        }

        Payment payment = Payment.builder()
                .lease(lease)
                .amount(paymentRequest.getAmount())
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.valueOf(String.valueOf(paymentRequest.getPaymentMethod())))
                .status(PaymentStatus.COMPLETED)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        try {

            ActivityLog paymentLog = ActivityLog.builder()
                    .user(lease.getTenant())
                    .actionType("PROCESS_PAYMENT")
                    .entityType("Payment")
                    .entityId(savedPayment.getPaymentId())
                    .details(String.format("{\"amount\":%.2f,\"property\":\"%s\"}",
                            savedPayment.getAmount(),
                            lease.getProperty().getAddress()))
                    .build();

            activityLogRepository.save(paymentLog);

            Notification tenantNotification = Notification.builder()
                    .user(lease.getTenant())
                    .title("Payment Confirmation")
                    .message(String.format("Your payment of $%.2f for %s has been processed successfully",
                            paymentRequest.getAmount(),
                            lease.getProperty().getAddress()))
                    .type(NotificationType.PAYMENT)
                    .status(NotificationStatus.UNREAD)
                    .build();
            notificationRepository.save(tenantNotification);

            Notification landlordNotification = Notification.builder()
                    .user(lease.getProperty().getOwner())
                    .title("New Payment Received")
                    .message(String.format("Payment of $%.2f received from tenant %s for property at %s",
                            paymentRequest.getAmount(),
                            lease.getTenant().getFirstName(),
                            lease.getProperty().getAddress()))
                    .type(NotificationType.PAYMENT)
                    .status(NotificationStatus.UNREAD)
                    .build();
            notificationRepository.save(landlordNotification);

            ActivityLog tenantNotificationLog = ActivityLog.builder()
                    .user(lease.getTenant())
                    .actionType("SEND_PAYMENT_NOTIFICATION")
                    .entityType("Notification")
                    .entityId(tenantNotification.getNotificationId())
                    .details("Payment confirmation sent to tenant")
                    .build();

            ActivityLog landlordNotificationLog = ActivityLog.builder()
                    .user(lease.getProperty().getOwner())
                    .actionType("SEND_PAYMENT_NOTIFICATION")
                    .entityType("Notification")
                    .entityId(landlordNotification.getNotificationId())
                    .details("Payment received notification sent to landlord")
                    .build();

            activityLogRepository.save(tenantNotificationLog);
            activityLogRepository.save(landlordNotificationLog);

        } catch (Exception e) {
            log.error("Failed to create payment notifications or activity logs: {}", e.getMessage());
        }

        return paymentConverter.toPaymentDTO(savedPayment);
    }

    public boolean isCurrentMonthPaid(Long leaseId) {
        YearMonth currentMonth = YearMonth.now();

        return paymentRepository.findByLeaseLeaseId(leaseId).stream()
                .map(payment -> YearMonth.from(payment.getPaymentDate()))
                .anyMatch(paymentMonth -> paymentMonth.equals(currentMonth));
    }

    public boolean isRentPaidForCurrentBillingCycle(Long leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        LocalDate startDate = lease.getStartDate();
        LocalDate today = LocalDate.now();

        LocalDate cycleStart = calculateCycleStart(startDate, today);
        LocalDate nextCycleStart = cycleStart.plusMonths(1);

        return paymentRepository.existsByLeaseAndPaymentDateBetween(
                lease,
                cycleStart,
                nextCycleStart
        );
    }

    private LocalDate calculateCycleStart(LocalDate leaseStartDate, LocalDate currentDate) {
        int monthsElapsed = (currentDate.getYear() - leaseStartDate.getYear()) * 12
                + currentDate.getMonthValue() - leaseStartDate.getMonthValue();

        LocalDate cycleStart = leaseStartDate.plusMonths(monthsElapsed);

        if (currentDate.isBefore(cycleStart)) {
            cycleStart = cycleStart.minusMonths(1);
        }

        return cycleStart;
    }

    public PaymentDTO confirmStripePayment(String sessionId) {
        Stripe.apiKey = apiKey;

        try {
            Session session = Session.retrieve(sessionId);
            String leaseIdStr = session.getMetadata().get("leaseId");

            if (leaseIdStr == null) throw new IllegalArgumentException("Missing leaseId metadata.");
            Long leaseId = Long.parseLong(leaseIdStr);

            Lease lease = leaseRepository.findById(leaseId)
                    .orElseThrow(() -> new RuntimeException("Lease not found"));

            if (isRentPaidForCurrentBillingCycle(leaseId)) {
                log.warn("Payment already exists for lease {} in current billing cycle", leaseId);
                throw new RuntimeException("Payment already processed for this billing cycle.");
            }

            Payment payment = Payment.builder()
                    .lease(lease)
                    .amount(lease.getMonthlyRent())
                    .paymentDate(LocalDate.now())
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .status(PaymentStatus.COMPLETED)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            return paymentConverter.toPaymentDTO(savedPayment);

        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve Stripe session: " + e.getMessage());
        }
    }

    public List<PaymentDTO> getPaymentsByLeaseId(Long leaseId) {
        leaseRepository.findById(leaseId)
                .orElseThrow(() -> {
                    log.error("Lease not found with ID: {}", leaseId);
                    return new RuntimeException("Lease not found");
                });

        List<Payment> payments = paymentRepository.findByLeaseLeaseId(leaseId);
        log.debug("Retrieved {} payments for lease ID: {}", payments.size(), leaseId);

        return payments.stream()
                .map(paymentConverter::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        log.debug("Retrieved all {} payments from system", payments.size());
        return payments.stream()
                .map(paymentConverter::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found with ID: {}", paymentId);
                    return new RuntimeException("Payment not found");
                });

        log.debug("Retrieved payment with ID: {}", paymentId);
        return paymentConverter.toPaymentDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByLandlordId(Long landlordId) {
        List<Payment> payments = paymentRepository.findPaymentByLandlordId(landlordId);
        log.debug("Retrieved {} payments for landlord ID: {}", payments.size(), landlordId);
        return payments.stream()
                .map(paymentConverter::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getLatestPaymentForLease(Long leaseId) {
        Payment latest = paymentRepository
                .findTopByLeaseLeaseIdOrderByPaymentDateDesc(leaseId)
                .orElseThrow(() -> new RuntimeException("No payments found for lease"));

        return paymentConverter.toPaymentDTO(latest);
    }

}