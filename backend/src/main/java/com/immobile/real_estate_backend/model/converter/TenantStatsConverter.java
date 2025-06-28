package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.TenantStatsDTO;
import com.immobile.real_estate_backend.model.entity.TenantStats;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TenantStatsConverter {

    public TenantStats toTenantStats(TenantStatsDTO tenantStatsDTO) {
        return TenantStats.builder()
                .tenantId(tenantStatsDTO.getTenantId())
                .paymentScore(tenantStatsDTO.getPaymentScore())
                .feedbackScore(tenantStatsDTO.getFeedbackScore())
                .overallScore(tenantStatsDTO.getOverallScore())
                .totalPayments(tenantStatsDTO.getTotalPayments())
                .latePayments(tenantStatsDTO.getLatePayments())
                .lastUpdated(tenantStatsDTO.getLastUpdated())
                .build();
    }

    public TenantStatsDTO toTenantStatsDTO(TenantStats tenantStats) {
        return TenantStatsDTO.builder()
                .tenantId(tenantStats.getTenantId())
                .paymentScore(tenantStats.getPaymentScore())
                .feedbackScore(tenantStats.getFeedbackScore())
                .overallScore(tenantStats.getOverallScore())
                .totalPayments(tenantStats.getTotalPayments())
                .latePayments(tenantStats.getLatePayments())
                .lastUpdated(tenantStats.getLastUpdated())
                .build();
    }
}