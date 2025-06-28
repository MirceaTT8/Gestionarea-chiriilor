package com.immobile.real_estate_backend.model.dto;

import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequestDTO {
    private Long requestId;
    private Long leaseId;
    private String description;
    private MaintenanceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> imageUrls;
    private BigDecimal cost;
    private Boolean isFixed;
}