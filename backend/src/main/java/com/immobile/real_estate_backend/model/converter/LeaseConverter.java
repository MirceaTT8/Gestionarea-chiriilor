package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Payment;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.PaymentStatus;
import com.immobile.real_estate_backend.model.enums.RentStatus;
import com.immobile.real_estate_backend.repository.PaymentRepository;
import com.immobile.real_estate_backend.repository.PropertyRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class LeaseConverter {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PaymentRepository paymentRepository;

    public Lease toLease(LeaseDTO leaseDTO) {
        User searchedTenant = userRepository.findById(leaseDTO.getTenantId()).orElseThrow();
        Property searchedProperty = propertyRepository.findById(leaseDTO.getPropertyId()).orElseThrow();

        return Lease.builder()
                .leaseId(leaseDTO.getLeaseId())
                .tenant(searchedTenant)
                .property(searchedProperty)
                .startDate(leaseDTO.getStartDate())
                .endDate(leaseDTO.getEndDate())
                .monthlyRent(leaseDTO.getMonthlyRent())
                .status(leaseDTO.getStatus())
                .createdAt(leaseDTO.getCreatedAt())
                .leaseTerminationStatus(leaseDTO.getTerminationStatus())
                .terminationRequestedAt(leaseDTO.getTerminationRequestedAt())
                .build();
    }

    public LeaseDTO toLeaseDTO(Lease lease) {
        Long tenantId = lease.getTenant() != null ? lease.getTenant().getUserId() : null;

        List<Payment> payments = paymentRepository.findByLeaseLeaseId(lease.getLeaseId());
        RentStatus rentStatus = calculateRentStatus(lease, payments);

        Payment latestPayment = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .max(Comparator.comparing(Payment::getPaymentDate))
                .orElse(null);

        return LeaseDTO.builder()
                .leaseId(lease.getLeaseId())
                .tenantId(tenantId)
                .propertyId(lease.getProperty().getPropertyId())
                .startDate(lease.getStartDate())
                .endDate(lease.getEndDate())
                .monthlyRent(lease.getMonthlyRent())
                .status(lease.getStatus())
                .createdAt(lease.getCreatedAt())
                .terminationStatus(lease.getLeaseTerminationStatus())
                .terminationRequestedAt(lease.getTerminationRequestedAt())
                .rentStatus(rentStatus)
                .latestPaymentDate(latestPayment != null ? latestPayment.getPaymentDate() : null)
                .build();
    }

    private RentStatus calculateRentStatus(Lease lease, List<Payment> payments) {
        int dueDay = lease.getStartDate().getDayOfMonth();
        LocalDate currentDate = LocalDate.now();
        LocalDate dueDate = LocalDate.of(
                currentDate.getYear(),
                currentDate.getMonth(),
                Math.min(dueDay, currentDate.lengthOfMonth())
        );

        if (payments.isEmpty()) {
            return currentDate.isAfter(dueDate) ? RentStatus.OVERDUE : RentStatus.UNPAID;
        }

        Payment latest = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .max(Comparator.comparing(Payment::getPaymentDate))
                .orElse(null);

        if (latest == null) {
            return currentDate.isAfter(dueDate) ? RentStatus.OVERDUE : RentStatus.UNPAID;
        }

        LocalDate paymentDate = latest.getPaymentDate();

        if (!paymentDate.isAfter(dueDate)) {
            return RentStatus.CURRENT;
        } else {
            return RentStatus.LATE;
        }
    }
}
