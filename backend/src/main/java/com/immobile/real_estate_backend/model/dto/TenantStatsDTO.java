package com.immobile.real_estate_backend.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantStatsDTO {
    private Long tenantId;
    private double paymentScore;
    private double feedbackScore;
    private double overallScore;
    private int totalPayments;
    private int latePayments;
    private LocalDateTime lastUpdated;
}