package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.Payment;
import com.immobile.real_estate_backend.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.lease = :leaseId " +
            "AND YEAR(p.paymentDate) = :year AND MONTH(p.paymentDate) = :month")
    Optional<Payment> findPaymentForMonth(Long leaseId, int year, int month);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.lease = :lease " +
            "AND YEAR(p.paymentDate) = :year " +
            "AND MONTH(p.paymentDate) = :month " +
            "AND p.status = :status")

    boolean existsPaymentForMonth(
            @Param("lease") Lease lease,
            @Param("year") int year,
            @Param("month") int month,
            @Param("status") PaymentStatus status);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.lease = :leaseId " +
            "AND YEAR(p.paymentDate) = :year AND MONTH(p.paymentDate) = :month " +
            "AND p.status = 'COMPLETED'")
    boolean existsCompletedPaymentForMonth(Long leaseId, int year, int month);

    @Query("""
        SELECT SUM(p.amount)
        FROM Payment p
        JOIN p.lease l
        JOIN l.property pr
        WHERE pr.owner.userId = :ownerId
            AND p.status = 'COMPLETED'
            AND p.paymentDate BETWEEN :start AND :end
    """)
    BigDecimal sumCompletedPaymentsByOwnerAndDateRange(
            @Param("ownerId") Long ownerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
            );
    List<Payment> findByLeaseLeaseId(Long leaseId);
    Optional<Payment> findByPaymentId(Long paymentId);

    @Query("SELECT p FROM Payment p WHERE p.lease.property.owner.userId = :landlordId")
    List<Payment> findPaymentByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT p FROM Payment p " +
            "JOIN p.lease l " +
            "JOIN l.property pr " +
            "JOIN pr.owner u " +
            "JOIN u.roles r " +
            "WHERE u.userId = :landlordId " +
            "AND r.name = 'landlord' " +
            "AND l.status = 'active'")
    List<Payment> findByLandlordIdWithActiveLeases(@Param("landlordId") Long landlordId);

    boolean existsByLeaseAndPaymentDateBetween(Lease lease, LocalDate paymentDate, LocalDate paymentDate2);

    Optional<Payment> findTopByLeaseLeaseIdOrderByPaymentDateDesc(Long leaseId);

    @Query("SELECT p FROM Payment p WHERE p.lease.tenant.userId = :tenantId AND p.status = 'COMPLETED'")
    List<Payment> findCompletedByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.lease l WHERE p.wasLate IS NULL")
    List<Payment> findAllWithLease();


}
