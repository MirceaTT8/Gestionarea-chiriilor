package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.dto.ReviewDTO;
import com.immobile.real_estate_backend.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
@AllArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> allReviews = reviewService.getAllReviews();
        return ResponseEntity.ok(allReviews);
    }

    @GetMapping("/landlord/reviewed-leases")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<Long>> getReviewedLeaseIds(Authentication authentication) {
        String landlordEmail = authentication.getName();
        List<Long> reviewedLeaseIds = reviewService.getReviewedLeaseIdsByLandlord(landlordEmail);
        return ResponseEntity.ok(reviewedLeaseIds);
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        String tenantEmail = authentication.getName();
        ReviewDTO createdReview = reviewService.createTenantReview(reviewDTO, tenantEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PostMapping("/landlord-to-tenant")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ReviewDTO> createLandlordReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        String landlordEmail = authentication.getName();
        ReviewDTO createdReview = reviewService.createLandlordReview(reviewDTO, landlordEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<LeaseDTO>> getPendingReviews(Authentication authentication) {
        String email = authentication.getName();
        List<LeaseDTO> pendingLeases = reviewService.getPendingReviewLeasesDTO(email);
        return ResponseEntity.ok(pendingLeases);
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Object>> getPendingReviewsCount(Authentication authentication) {
        String email = authentication.getName();
        int count = reviewService.getPendingReviewsCount(email);
        boolean hasPending = reviewService.hasPendingReviews(email);

        return ResponseEntity.ok(Map.of(
                "count", count,
                "hasPendingReviews", hasPending
        ));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProperty(@PathVariable Long propertyId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByPropertyId(propertyId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByTenant(@PathVariable Long tenantId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByTenantId(tenantId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByLandlord(@PathVariable Long landlordId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByLandlordId(landlordId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-reviewed/{propertyId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, Boolean>> checkIfPropertyReviewed(
            @PathVariable Long propertyId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isReviewed = reviewService.hasReviewedProperty(email, propertyId);
        return ResponseEntity.ok(Map.of("reviewed", isReviewed));
    }
}