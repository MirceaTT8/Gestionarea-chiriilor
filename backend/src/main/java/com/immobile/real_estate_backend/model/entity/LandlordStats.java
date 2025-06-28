package com.immobile.real_estate_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "landlord_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandlordStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "landlord_id")
    private Long landlordId;

    @Column(name = "rating_score")
    private double ratingScore;

    @Column(name = "maintenance_score")
    private double maintenanceScore;

    @Column(name = "behavior_score")
    private double behaviorScore;

    @Column(name = "overall_score")
    private double overallScore;

    @Column(name = "completed_leases")
    private int completedLeases;

    @Column(name = "avg_maintenance_response_time")
    private long avgMaintenanceResponseTime;

    @Column(name = "flagged_properties")
    private int flaggedProperties;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Version
    private Long version;
}
