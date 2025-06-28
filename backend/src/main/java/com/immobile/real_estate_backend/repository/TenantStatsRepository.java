package com.immobile.real_estate_backend.repository;
import com.immobile.real_estate_backend.model.entity.TenantStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantStatsRepository extends JpaRepository<TenantStats, Long> {
    Optional<TenantStats> findByTenantId(Long tenantId);
}
