package com.immobile.real_estate_backend.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDTO {
    private Long id;
    private Long userId;
    private String actionType;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime createdAt;
}
