package com.immobile.real_estate_backend.model.dto;

import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaseRequestDTO {
    private Long propertyId;
    private String tenantEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
}

