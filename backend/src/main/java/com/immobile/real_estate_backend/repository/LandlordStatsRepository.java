package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.LandlordStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandlordStatsRepository extends JpaRepository<LandlordStats, Long> {
}
