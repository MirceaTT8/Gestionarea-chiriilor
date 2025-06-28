package com.immobile.real_estate_backend.model.entity;

import com.immobile.real_estate_backend.model.enums.ReviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private Lease lease;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "display_name", length = 50)
    private String displayName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type")
    @Builder.Default
    private ReviewType reviewType = ReviewType.TENANT_TO_LANDLORD;

    @PrePersist
    protected void prePersist() {
        if (this.displayName == null && tenant != null && property != null) {
            this.displayName = "Tenant-" + Objects.hash(tenant.getUserId(), property.getPropertyId());
        }
        if (this.landlord == null && property != null) {
            this.landlord = property.getOwner();
        }
    }

    @Override
    public String toString() {
        return "Review:" + reviewId + "|Property:" + property.getPropertyId();
    }
}

