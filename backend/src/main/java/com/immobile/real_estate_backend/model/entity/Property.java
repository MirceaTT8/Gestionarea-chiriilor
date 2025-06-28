package com.immobile.real_estate_backend.model.entity;

import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.PropertyType;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @Column(name = "property_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Column(name = "rent_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Column(precision = 9)
    private Double latitude;

    @Column(precision = 9)
    private Double longitude;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "property_status", nullable = false)
    @org.hibernate.annotations.ColumnDefault("'INACTIVE'")
    private PropertyStatus status = PropertyStatus.INACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    @org.hibernate.annotations.ColumnDefault("'PENDING'")
    private ValidationStatus validationStatus = ValidationStatus.PENDING;


    @Column(name = "is_flagged", nullable = false)
    @org.hibernate.annotations.ColumnDefault("false")
    private Boolean isFlagged = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProperty(this);
    }
}

