package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.ActivityLogDTO;
import com.immobile.real_estate_backend.model.entity.ActivityLog;
import com.immobile.real_estate_backend.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ActivityLogConverter {

    public ActivityLogDTO toDTO(ActivityLog log) {
        return ActivityLogDTO.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getUserId() : null)
                .actionType(log.getActionType())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public ActivityLog toEntity(ActivityLogDTO dto, User user) {
        return ActivityLog.builder()
                .id(dto.getId())
                .user(user)
                .actionType(dto.getActionType())
                .entityType(dto.getEntityType())
                .entityId(dto.getEntityId())
                .details(dto.getDetails())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
