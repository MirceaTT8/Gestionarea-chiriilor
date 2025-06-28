package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop10ByUserUserIdOrderByCreatedAtDesc(Long userId);
}
