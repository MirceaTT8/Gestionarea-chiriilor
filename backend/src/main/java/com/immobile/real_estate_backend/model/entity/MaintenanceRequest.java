package com.immobile.real_estate_backend.model.entity;

import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lease lease;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "is_fixed")
    private Boolean isFixed;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "maintenanceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Image> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addImage(Image image) {
        images.add(image);
        image.setMaintenanceRequest(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setMaintenanceRequest(null);
    }


    public void markAsNotFixed() {
        if (this.status == MaintenanceStatus.COMPLETED) {
            this.isFixed = false;
            this.status = MaintenanceStatus.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }


    public void markAsFixed() {
        this.isFixed = true;
        this.status = MaintenanceStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
}