package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.TenantStatsConverter;
import com.immobile.real_estate_backend.model.dto.TenantStatsDTO;
import com.immobile.real_estate_backend.model.entity.Payment;
import com.immobile.real_estate_backend.model.entity.Review;
import com.immobile.real_estate_backend.model.entity.TenantStats;
import com.immobile.real_estate_backend.model.enums.ReviewType;
import com.immobile.real_estate_backend.repository.PaymentRepository;
import com.immobile.real_estate_backend.repository.ReviewRepository;
import com.immobile.real_estate_backend.repository.TenantStatsRepository;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantScoreService {

    private final PaymentRepository paymentRepository;
    private final TenantStatsRepository tenantStatsRepository;
    private final LeaseRepository leaseRepository;
    private final ReviewRepository reviewRepository;

    private static final double WEIGHT_PAYMENT = 0.6;
    private static final double WEIGHT_FEEDBACK = 0.4;
    private final TenantStatsConverter tenantStatsConverter;

    @Transactional
    public void updateScoreForTenant(Long tenantId) {
        double paymentScore = getPaymentPunctualityScore(tenantId);
        double feedbackScore = getFeedbackScore(tenantId);
        double overallScore = (paymentScore * WEIGHT_PAYMENT) + (feedbackScore * WEIGHT_FEEDBACK);

        List<Payment> payments = paymentRepository.findCompletedByTenantId(tenantId);
        int totalPayments = payments.size();
        int latePayments = (int) payments.stream().filter(p -> Boolean.TRUE.equals(p.getWasLate())).count();
        int onTimePayments = totalPayments - latePayments;
        double punctualityRatio = totalPayments > 0 ? (double) onTimePayments / totalPayments : 0.0;

        int activeLeases = getActiveLeaseCount(tenantId);
        int completedLeases = getCompletedLeaseCount(tenantId);

        TenantStats stats = tenantStatsRepository.findByTenantId(tenantId)
                .orElse(TenantStats.builder().tenantId(tenantId).build());

        stats.setPaymentScore(paymentScore);
        stats.setFeedbackScore(feedbackScore);
        stats.setOverallScore(overallScore);
        stats.setTotalPayments(totalPayments);
        stats.setLatePayments(latePayments);
        stats.setOnTimePayments(onTimePayments);
        stats.setPaymentPunctualityRatio(punctualityRatio);
        stats.setActiveLeases(activeLeases);
        stats.setCompletedLeases(completedLeases);
        stats.setLastUpdated(LocalDateTime.now());

        tenantStatsRepository.save(stats);
    }

    public List<TenantStatsDTO> getAllTenantStats() {
        return tenantStatsRepository.findAll()
                .stream()
                .map(tenantStatsConverter::toTenantStatsDTO)
                .collect(Collectors.toList());
    }

    public TenantStatsDTO getTenantStats(Long tenantId) {
        return tenantStatsRepository.findById(tenantId)
                .map(tenantStatsConverter::toTenantStatsDTO)
                .orElse(null);
    }

    public double calculateTenantScore(Long tenantId) {
        double paymentScore = getPaymentPunctualityScore(tenantId);
        double feedbackScore = getFeedbackScore(tenantId);

        return (paymentScore * WEIGHT_PAYMENT) + (feedbackScore * WEIGHT_FEEDBACK);
    }

    public double getPaymentPunctualityScore(Long tenantId) {
        List<Payment> payments = paymentRepository.findCompletedByTenantId(tenantId);

        if (payments.isEmpty()) return 3.0;

        long lateCount = payments.stream().filter(p -> Boolean.TRUE.equals(p.getWasLate())).count();
        if(lateCount == 0) return 5.0;
        double lateRatio = (double) lateCount / payments.size();

        System.out.println(lateRatio);

        if (lateRatio <= 0.05) return 5.0;
        else if (lateRatio <= 0.15) return 4.0;
        else if (lateRatio <= 0.3) return 3.0;
        else if (lateRatio <= 0.5) return 2.0;
        else return 1.0;
    }

    public double getFeedbackScore(Long tenantId) {
        List<Review> reviews = reviewRepository.findLandlordReviewsAboutTenant(tenantId);

        if (reviews.isEmpty()) {
            System.out.println("No landlord-to-tenant reviews found for tenant " + tenantId);
            return 3.5;
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(3.5);

        System.out.println("Tenant " + tenantId + " has " + reviews.size() +
                " landlord reviews with average rating: " + averageRating);

        return averageRating;
    }

    private int getActiveLeaseCount(Long tenantId) {
         return leaseRepository.countActiveLeasesByTenantId(tenantId);
    }

    private int getCompletedLeaseCount(Long tenantId) {
         return leaseRepository.countCompletedLeasesByTenantId(tenantId);
    }
}