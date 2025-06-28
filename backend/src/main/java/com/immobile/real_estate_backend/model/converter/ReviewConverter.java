package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.ReviewDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.ReviewType;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import com.immobile.real_estate_backend.repository.PropertyRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewConverter {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;

    public Review toReview(ReviewDTO dto) {
        User tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Lease lease = leaseRepository.findById(dto.getLeaseId())
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        User landlord = dto.getLandlordId() != null
                ? userRepository.findById(dto.getLandlordId())
                .orElse(property.getOwner())
                : property.getOwner();

        return Review.builder()
                .reviewId(dto.getReviewId())
                .tenant(tenant)
                .property(property)
                .landlord(landlord)
                .lease(lease)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .displayName(dto.getDisplayName())
                .reviewType(dto.getReviewType())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public ReviewDTO toReviewDTO(Review review) {
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .tenantId(review.getTenant().getUserId())
                .propertyId(review.getProperty().getPropertyId())
                .landlordId(review.getLandlord().getUserId())
                .leaseId(review.getLease().getLeaseId())
                .displayName(review.getDisplayName())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewType(review.getReviewType())
                .createdAt(review.getCreatedAt())
                .build();
    }
}