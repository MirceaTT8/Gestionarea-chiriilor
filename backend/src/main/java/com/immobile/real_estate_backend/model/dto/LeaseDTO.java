package com.immobile.real_estate_backend.model.dto;

import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.LeaseTerminationStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.immobile.real_estate_backend.model.enums.RentStatus; // Make sure to import

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaseDTO {
    private Long leaseId;
    private Long tenantId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private LeaseStatus status;
    private LocalDate createdAt;
    private LeaseTerminationStatus terminationStatus;
    private LocalDate terminationRequestedAt;
    private RentStatus rentStatus;
    private LocalDate latestPaymentDate;
}

