package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.PaymentDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Payment;
import com.immobile.real_estate_backend.model.enums.PaymentMethod;
import com.immobile.real_estate_backend.model.enums.PaymentStatus;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class PaymentConverter {
    private final LeaseRepository leaseRepository;

    public Payment toPayment(PaymentDTO paymentDTO) {
        Lease lease = leaseRepository.findById(paymentDTO.getLeaseId())
                .orElseThrow(() -> new RuntimeException("Lease not found with id: " + paymentDTO.getLeaseId()));

        return Payment.builder()
                .paymentId(paymentDTO.getPaymentId())
                .lease(lease)
                .amount(paymentDTO.getAmount())
                .paymentDate(paymentDTO.getPaymentDate() != null ?
                        paymentDTO.getPaymentDate() : LocalDate.now())
                .paymentMethod(PaymentMethod.valueOf(String.valueOf(paymentDTO.getPaymentMethod())))
                .status(PaymentStatus.valueOf(String.valueOf(paymentDTO.getStatus())))
                .build();
    }

    public PaymentDTO toPaymentDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .leaseId(payment.getLease().getLeaseId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(PaymentMethod.valueOf(payment.getPaymentMethod().name()))
                .status(PaymentStatus.valueOf(payment.getStatus().name()))
                .build();
    }
}