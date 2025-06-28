package com.immobile.real_estate_backend.model.entity;


import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.LeaseTerminationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "leases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "monthly_rent",nullable = false)
    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    private LeaseStatus status;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'PENDING'")
    private LeaseTerminationStatus leaseTerminationStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "termination_requested_at", updatable = false)
    private LocalDate terminationRequestedAt;

    @OneToMany(mappedBy = "lease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Lease:" + leaseId;
    }

}
