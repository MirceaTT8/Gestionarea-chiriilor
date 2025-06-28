package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.MaintenanceRequestDTO;
import com.immobile.real_estate_backend.model.entity.Image;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.MaintenanceRequest;
import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MaintenanceRequestConverter {
    private final LeaseRepository leaseRepository;

    public MaintenanceRequest toMaintenanceRequest(MaintenanceRequestDTO dto) {
        Lease lease = leaseRepository.findById(dto.getLeaseId())
                .orElseThrow(() -> new RuntimeException("Lease not found with id: " + dto.getLeaseId()));

        return MaintenanceRequest.builder()
                .requestId(dto.getRequestId())
                .lease(lease)
                .description(dto.getDescription())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .cost(dto.getCost())
                .isFixed(dto.getIsFixed() != null ? dto.getIsFixed() : false)
                .build();
    }

    public MaintenanceRequestDTO toMaintenanceRequestDTO(MaintenanceRequest request) {
        return MaintenanceRequestDTO.builder()
                .requestId(request.getRequestId())
                .leaseId(request.getLease().getLeaseId())
                .description(request.getDescription())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .cost(request.getCost())
                .isFixed(request.getIsFixed())
                .imageUrls(request.getImages().stream()
                        .map(Image::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}