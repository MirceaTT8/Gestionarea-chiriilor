package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("SELECT p FROM Property p WHERE p.owner.userId = :userId")
    List<Property> getPropertiesByOwner(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Lease l " +
            "WHERE l.property.propertyId = :propertyId " +
            "AND l.tenant.userId = :tenantId " +
            "AND l.status = 'active'")
    boolean existsByPropertyId(@Param("propertyId") Long propertyId,
                               @Param("tenantId") Long tenantId);

    List<Property> getPropertiesByOwner(User owner);

    List<Property> findPropertyByOwnerUserId(Long userId);

    @Query("""
    SELECT COUNT(p)
    FROM Property p
    WHERE p.owner.userId = :ownerId AND p.isFlagged = true
""")
    int countFlaggedPropertiesByOwnerId(@Param("ownerId") Long ownerId);

    List<Property> findByValidationStatus(ValidationStatus validationStatus);
}
