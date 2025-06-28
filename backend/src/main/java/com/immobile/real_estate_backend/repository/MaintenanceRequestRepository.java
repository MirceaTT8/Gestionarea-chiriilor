package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    @EntityGraph(attributePaths = "images")
    List<MaintenanceRequest> findByLeaseLeaseId(Long leaseId);

    @Query("SELECT mr FROM MaintenanceRequest mr " +
            "JOIN mr.lease l " +
            "JOIN l.property p " +
            "WHERE p.owner.userId = :ownerId")
    List<MaintenanceRequest> findByPropertyOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT r FROM MaintenanceRequest r " +
            "JOIN r.lease l " +
            "JOIN l.property p " +
            "WHERE p.owner.userId = :ownerId")
    List<MaintenanceRequest> findAllByLeasePropertyOwnerUserId(@Param("ownerId") Long ownerId);

    @Query("""
    SELECT m
    FROM MaintenanceRequest m
    WHERE m.lease.property.propertyId IN :propertyIds
""")
    List<MaintenanceRequest> findByPropertyIds(@Param("propertyIds") List<Long> propertyIds);




}
