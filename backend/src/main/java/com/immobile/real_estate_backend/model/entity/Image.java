package com.immobile.real_estate_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private byte[] content;
    private String name;
    @Column(name = "content_type")
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_request_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MaintenanceRequest maintenanceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Property property;

    @PrePersist
    @PreUpdate
    private void validateAssociation() {
        if (maintenanceRequest != null && property != null) {
            throw new IllegalStateException("Image can only be associated with either a maintenance request or a property");
        }
    }

}