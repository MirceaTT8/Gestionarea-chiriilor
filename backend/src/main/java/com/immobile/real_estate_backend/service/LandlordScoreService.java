package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import com.immobile.real_estate_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LandlordScoreService {

    private final ReviewRepository reviewRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final LandlordStatsRepository landlordStatsRepository;
    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;

    private static final double WEIGHT_RATING = 0.5;
    private static final double WEIGHT_MAINTENANCE = 0.3;
    private static final double WEIGHT_BEHAVIOR = 0.2;

    public List<LandlordStats> getAllScores() {
        return landlordStatsRepository.findAll();
    }

    @Transactional
    public void updateScoreForLandlord(Long landlordId) {

        double ratingScore = getRatingScore(landlordId);
        double maintenanceScore = getMaintenanceScore(landlordId);
        double behaviorScore = getAppBehaviorScore(landlordId);

        double overallScore = (ratingScore * WEIGHT_RATING)
                + (maintenanceScore * WEIGHT_MAINTENANCE)
                + (behaviorScore * WEIGHT_BEHAVIOR);

        int completedLeases = leaseRepository.countTerminatedLeases(landlordId);
        long avgResponseTime = calculateAvgResponseTime(landlordId);
        int flaggedProperties = propertyRepository.countFlaggedPropertiesByOwnerId(landlordId);

        LandlordStats stats = landlordStatsRepository.findById(landlordId)
                .orElse(LandlordStats.builder()
                        .landlordId(landlordId)
                        .build());

        stats.setRatingScore(ratingScore);
        stats.setMaintenanceScore(maintenanceScore);
        stats.setBehaviorScore(behaviorScore);
        stats.setOverallScore(overallScore);
        stats.setCompletedLeases(completedLeases);
        stats.setAvgMaintenanceResponseTime(avgResponseTime);
        stats.setFlaggedProperties(flaggedProperties);
        stats.setLastUpdated(LocalDateTime.now());

        landlordStatsRepository.save(stats);
    }

    public double calculateLandlordScore(Long landlordId) {
        double ratingScore = getRatingScore(landlordId);
        double maintenanceScore = getMaintenanceScore(landlordId);
        double behaviorScore = getAppBehaviorScore(landlordId);
        
        return (
                (ratingScore * WEIGHT_RATING) +
                        (maintenanceScore * WEIGHT_MAINTENANCE) +
                        (behaviorScore * WEIGHT_BEHAVIOR)
        );
    }

    public double getRatingScore(Long landlordId) {
        List<Review> reviews = reviewRepository.findTenantReviewsAboutLandlord(landlordId);

        if (reviews.isEmpty()) {
            System.out.println("No tenant-to-landlord reviews found for landlord " + landlordId);
            return 3.5;
        }

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(3.5);
    }

    public double getMaintenanceScore(Long landlordId) {

        long avgHours = calculateAvgResponseTime(landlordId);

        if (avgHours == 0) return 5.0;

        if (avgHours <= 24) return 5.0;
        else if (avgHours <= 72) return 4.0;
        else if (avgHours <= 120) return 3.0;
        else if (avgHours <= 168) return 2.0;
        else return 1.0;
    }


    public double getAppBehaviorScore(Long landlordId) {
        int badFlags = propertyRepository.countFlaggedPropertiesByOwnerId(landlordId);
        return switch (badFlags) {
            case 0, 1 -> 5.0;
            case 2 -> 4.0;
            case 3 -> 3.0;
            case 4 -> 2.0;
            default -> 1.0;
        };
    }

    public long calculateAvgResponseTime(Long landlordId) {
        List<Property> properties = propertyRepository.findPropertyByOwnerUserId(landlordId);
        List<Long> propertyIds = properties.stream().map(Property::getPropertyId).toList();

        List<MaintenanceRequest> requests = maintenanceRequestRepository.findByPropertyIds(propertyIds).stream()
                .filter(r -> r.getStatus() == MaintenanceStatus.COMPLETED)
                .toList();

        if (requests.isEmpty()) return 0;

        return Math.round(requests.stream()
                .mapToLong(r -> ChronoUnit.HOURS.between(r.getCreatedAt(), r.getUpdatedAt()))
                .average()
                .orElse(0));
    }

}

