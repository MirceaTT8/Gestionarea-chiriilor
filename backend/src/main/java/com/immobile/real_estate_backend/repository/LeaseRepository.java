package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.LeaseTerminationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    Optional<Lease> findByLeaseIdAndTenantUserId(Long leaseId, Long tenantId);

    @Query("SELECT l FROM Lease l " +
            "JOIN Property p ON l.property.propertyId = p.propertyId " +
            "JOIN User u ON p.owner.userId = u.userId " +
            "JOIN u.roles r " +
            "WHERE u.userId = :ownerId AND l.status = 'active' AND r.name = 'landlord' ")
    List<Lease> findActiveLeasesByLandlord(@Param("ownerId") Long ownerId);

    Optional<Lease> findFirstByTenantAndStatus(User tenant, LeaseStatus status);

    @Query("SELECT l FROM Lease l WHERE l.property.owner = :owner")
    List<Lease> findByPropertyOwner(@Param("owner") User owner);

    List<Lease> findAllByLeaseTerminationStatus(LeaseTerminationStatus leaseTerminationStatus);

    List<Lease> findAllByStatus(LeaseStatus leaseStatus);

    @Query("SELECT l FROM Lease l WHERE l.startDate IS NOT NULL")
    List<Lease> findAllWithStartDates();

    @Query("""
    SELECT COUNT(l) FROM Lease l
    WHERE l.property.owner.userId = :landlordId
      AND l.status = 'TERMINATED'
      AND l.leaseTerminationStatus = 'APPROVED'
""")
    int countTerminatedLeases(Long landlordId);

    boolean existsByTenantUserIdAndPropertyPropertyIdAndStatus(Long tenantId, Long propertyId, LeaseStatus status);

    @Query("SELECT COUNT(l) FROM Lease l WHERE l.tenant.userId = :tenantId AND l.status = 'ACTIVE'")
    int countActiveLeasesByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(l) FROM Lease l WHERE l.tenant.userId = :tenantId AND l.status = 'TERMINATED'")
    int countCompletedLeasesByTenantId(@Param("tenantId") Long tenantId);

    @Query("""
        SELECT l FROM Lease l 
        WHERE l.status = 'ACTIVE' 
        AND l.endDate IS NOT NULL 
        AND l.endDate <= :currentDate
        ORDER BY l.endDate ASC
    """)
    List<Lease> findActiveExpiredLeases(@Param("currentDate") LocalDate currentDate);

    @Query("""
        SELECT l FROM Lease l 
        WHERE l.status = 'ACTIVE' 
        AND l.endDate IS NOT NULL 
        AND l.endDate BETWEEN :startDate AND :endDate
        ORDER BY l.endDate ASC
    """)
    List<Lease> findLeasesExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
        SELECT l FROM Lease l 
        WHERE l.status = 'ACTIVE' 
        AND l.endDate IS NOT NULL 
        AND l.endDate BETWEEN :startDate AND :endDate
        ORDER BY l.endDate ASC
    """)
    List<Lease> findLeasesExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Lease> findAllByTenant(User tenant);

    @Query("SELECT l FROM Lease l WHERE l.tenant.userId = :tenantId ORDER BY l.createdAt DESC")
    List<Lease> findAllByTenantUserId(@Param("tenantId") Long tenantId);

    @Query("SELECT COUNT(l) > 0 FROM Lease l WHERE l.tenant.userId = :tenantId")
    boolean existsByTenantUserId(@Param("tenantId") Long tenantId);

    List<Lease> findAllByTenantAndStatus(User tenant, LeaseStatus status);
    boolean existsByTenantAndPropertyAndStatus(User tenant, Property property, LeaseStatus status);


}
