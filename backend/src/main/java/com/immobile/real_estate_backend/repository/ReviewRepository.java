package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Review;
import com.immobile.real_estate_backend.model.enums.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.immobile.real_estate_backend.model.enums.ReviewStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyPropertyId(Long propertyId);
    List<Review> findByTenantUserId(Long propertyId);
    List<Review> findByLandlordUserId(Long propertyId);
    boolean existsByTenantUserIdAndPropertyPropertyId(Long tenantId, Long propertyId);

    List<Review> findByReviewType(ReviewType reviewType);
    List<Review> findByTenantUserIdAndReviewType(Long tenantId, ReviewType reviewType);
    List<Review> findByLandlordUserIdAndReviewType(Long landlordId, ReviewType reviewType);
    boolean existsByLandlordUserIdAndTenantUserIdAndPropertyPropertyIdAndReviewType(
            Long landlordId, Long tenantId, Long propertyId, ReviewType reviewType);
    boolean existsByTenantUserIdAndPropertyPropertyIdAndReviewType(Long tenantId, Long propertyId, ReviewType reviewType);

    @Query("SELECT r FROM Review r WHERE r.landlord.userId = :landlordId AND r.reviewType = 'TENANT_TO_LANDLORD'")
    List<Review> findTenantReviewsAboutLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT r FROM Review r WHERE r.tenant.userId = :tenantId AND r.reviewType = 'LANDLORD_TO_TENANT'")
    List<Review> findLandlordReviewsAboutTenant(@Param("tenantId") Long tenantId);

}
