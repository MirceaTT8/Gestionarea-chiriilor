package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.ReviewConverter;
import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.dto.ReviewDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.Review;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.ReviewType;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import com.immobile.real_estate_backend.repository.PropertyRepository;
import com.immobile.real_estate_backend.repository.ReviewRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final LeaseRepository leaseRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseService leaseService;

    public List<ReviewDTO> getAllReviews() {
        try {
            List<Review> allReviews = reviewRepository.findAll();

            return allReviews.stream()
                    .map(reviewConverter::toReviewDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all reviews for admin: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    @Transactional
    public ReviewDTO createTenantReview(ReviewDTO reviewDTO, String tenantEmail) {
        log.info("Creating tenant-to-landlord review for tenant {} and property {}", tenantEmail, reviewDTO.getPropertyId());

        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new RuntimeException("Tenant not found with email: " + tenantEmail));
        reviewDTO.setTenantId(tenant.getUserId());
        reviewDTO.setReviewType(ReviewType.TENANT_TO_LANDLORD);

        Property property = propertyRepository.findById(reviewDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + reviewDTO.getPropertyId()));

        reviewDTO.setLandlordId(property.getOwner().getUserId());

        boolean alreadyReviewed = reviewRepository.existsByTenantUserIdAndPropertyPropertyIdAndReviewType(
                tenant.getUserId(), reviewDTO.getPropertyId(), ReviewType.TENANT_TO_LANDLORD);
        if (alreadyReviewed) {
            log.warn("Tenant {} has already reviewed property {}", tenant.getUserId(), reviewDTO.getPropertyId());
            throw new IllegalStateException("User has already reviewed this property");
        }

        List<Lease> tenantLeases = leaseRepository.findAllByTenant(tenant);
        boolean hasTerminatedLease = tenantLeases.stream()
                .anyMatch(lease -> {
                    boolean propertyMatches = lease.getProperty().getPropertyId().equals(reviewDTO.getPropertyId());
                    boolean isTerminated = lease.getStatus() == LeaseStatus.TERMINATED;
                    if (propertyMatches) {
                        log.info("Found lease {} for property {} with status: {}",
                                lease.getLeaseId(), reviewDTO.getPropertyId(), lease.getStatus());
                    }
                    return propertyMatches && isTerminated;
                });

        if (!hasTerminatedLease) {
            throw new IllegalStateException("User can only review a property after the lease is terminated.");
        }

        Review review = reviewConverter.toReview(reviewDTO);
        review = reviewRepository.save(review);

        return reviewConverter.toReviewDTO(review);
    }

    @Transactional
    public ReviewDTO createLandlordReview(ReviewDTO reviewDTO, String landlordEmail) {

        User landlord = userRepository.findByEmail(landlordEmail)
                .orElseThrow(() -> new RuntimeException("Landlord not found with email: " + landlordEmail));
        reviewDTO.setLandlordId(landlord.getUserId());
        reviewDTO.setReviewType(ReviewType.LANDLORD_TO_TENANT);

        User tenant = userRepository.findById(reviewDTO.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + reviewDTO.getTenantId()));

        Property property = propertyRepository.findById(reviewDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + reviewDTO.getPropertyId()));

        if (!property.getOwner().getUserId().equals(landlord.getUserId())) {
            throw new IllegalStateException("Landlord can only review tenants for their own properties");
        }

        boolean alreadyReviewed = reviewRepository.existsByLandlordUserIdAndTenantUserIdAndPropertyPropertyIdAndReviewType(
                landlord.getUserId(), tenant.getUserId(), property.getPropertyId(), ReviewType.LANDLORD_TO_TENANT);
        if (alreadyReviewed) {
            throw new IllegalStateException("Landlord has already reviewed this tenant for this property");
        }

        List<Lease> tenantLeases = leaseRepository.findAllByTenant(tenant);

        Lease terminatedLease = tenantLeases.stream()
                .filter(lease -> {
                    boolean propertyMatches = lease.getProperty().getPropertyId().equals(reviewDTO.getPropertyId());
                    boolean isTerminated = lease.getStatus() == LeaseStatus.TERMINATED;
                    return propertyMatches && isTerminated;
                })
                .findFirst()
                .orElse(null);

        if (terminatedLease == null) {
            throw new IllegalStateException("Landlord can only review a tenant after the lease is terminated.");
        }

        reviewDTO.setLeaseId(terminatedLease.getLeaseId());

        Review review = reviewConverter.toReview(reviewDTO);
        review = reviewRepository.save(review);

        return reviewConverter.toReviewDTO(review);
    }

    @Deprecated
    public ReviewDTO createReview(ReviewDTO reviewDTO, String tenantEmail) {
        return createTenantReview(reviewDTO, tenantEmail);
    }

    public List<LeaseDTO> getPendingReviewLeasesDTO(String tenantEmail) {
        try {
            User tenant = userRepository.findByEmail(tenantEmail)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with email: " + tenantEmail));

            List<LeaseDTO> terminatedLeases = leaseService.getTerminatedLeasesByTenantEmail(tenantEmail);

            List<LeaseDTO> pendingLeases = terminatedLeases.stream()
                    .filter(leaseDTO -> !reviewRepository.existsByTenantUserIdAndPropertyPropertyId(
                            tenant.getUserId(), leaseDTO.getPropertyId()))
                    .collect(Collectors.toList());

            return pendingLeases;

        } catch (Exception e) {
            log.error("Error fetching pending review leases DTO for tenant {}: {}", tenantEmail, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public boolean hasPendingReviews(String tenantEmail) {
        try {
            return getPendingReviewsCount(tenantEmail) > 0;
        } catch (Exception e) {
            log.error("Error checking pending reviews for tenant {}: {}", tenantEmail, e.getMessage(), e);
            return false;
        }
    }

    public int getPendingReviewsCount(String tenantEmail) {
        try {
            return getPendingReviewLeasesDTO(tenantEmail).size();
        } catch (Exception e) {
            log.error("Error counting pending reviews for tenant {}: {}", tenantEmail, e.getMessage(), e);
            return 0;
        }
    }

    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        return reviewConverter.toReviewDTO(review);
    }

    public List<ReviewDTO> getReviewsByPropertyId(Long propertyId) {
        return reviewRepository.findByPropertyPropertyId(propertyId).stream()
                .map(reviewConverter::toReviewDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByTenantId(Long tenantId) {
        return reviewRepository.findByTenantUserId(tenantId).stream()
                .map(reviewConverter::toReviewDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByLandlordId(Long landlordId) {
        return reviewRepository.findByLandlordUserId(landlordId).stream()
                .map(reviewConverter::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());
        existingReview.setDisplayName(reviewDTO.getDisplayName());

        Review updatedReview = reviewRepository.save(existingReview);
        return reviewConverter.toReviewDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<Long> getReviewedLeaseIdsByLandlord(String landlordEmail) {
        try {
            User landlord = userRepository.findByEmail(landlordEmail)
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            List<Review> reviews = reviewRepository.findByLandlordUserIdAndReviewType(
                    landlord.getUserId(), ReviewType.LANDLORD_TO_TENANT);

            return reviews.stream()
                    .map(review -> review.getLease().getLeaseId())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching reviewed lease IDs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean hasReviewedProperty(String tenantEmail, Long propertyId) {
        try {
            User tenant = userRepository.findByEmail(tenantEmail)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with email: " + tenantEmail));

            return reviewRepository.existsByTenantUserIdAndPropertyPropertyIdAndReviewType(
                    tenant.getUserId(), propertyId, ReviewType.TENANT_TO_LANDLORD);
        } catch (Exception e) {
            log.error("Error checking if tenant {} has reviewed property {}: {}",
                    tenantEmail, propertyId, e.getMessage(), e);
            return false;
        }
    }

}