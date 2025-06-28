package com.immobile.real_estate_backend.model.dto;

import com.immobile.real_estate_backend.model.enums.ReviewStatus;
import com.immobile.real_estate_backend.model.enums.ReviewType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long reviewId;
    private Long tenantId;
    private Long propertyId;
    private Long landlordId;
    private Long leaseId;
    private String displayName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private ReviewType reviewType;
}
