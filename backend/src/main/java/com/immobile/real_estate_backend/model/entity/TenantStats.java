package com.immobile.real_estate_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantStats {

    @Id
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "payment_score")
    private double paymentScore;

    @Column(name = "feedback_score")
    private double feedbackScore;

    @Column(name = "overall_score")
    private double overallScore;

    @Column(name = "total_payments")
    private int totalPayments;

    @Column(name = "late_payments")
    private int latePayments;

    @Column(name = "on_time_payments")
    private int onTimePayments;

    @Column(name = "payment_punctuality_ratio")
    private double paymentPunctualityRatio;

    @Column(name = "active_leases")
    private int activeLeases;

    @Column(name = "completed_leases")
    private int completedLeases;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Version
    private Long version;
}