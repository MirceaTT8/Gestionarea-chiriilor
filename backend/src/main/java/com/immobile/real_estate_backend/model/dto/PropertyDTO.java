package com.immobile.real_estate_backend.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.PropertyType;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {
    private Long propertyId;
    @JsonAlias("owner_id")
    private Long ownerId;
    private String name;
    private String address;
    private PropertyType type;
    @JsonAlias("rent_amount")
    private BigDecimal rentAmount;
    private PropertyStatus status;
    private Double longitude;
    private Double latitude;
    private LocalDateTime createdAt;
    private List<Long> imageUrls;
    private ValidationStatus validationStatus;
    private Boolean isFlagged;
}
