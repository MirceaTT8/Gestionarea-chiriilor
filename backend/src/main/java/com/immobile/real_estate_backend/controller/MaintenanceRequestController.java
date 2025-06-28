package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.MaintenanceRequestDTO;
import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import com.immobile.real_estate_backend.service.MaintenanceRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@AllArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getAllMaintenanceRequests() {
        List<MaintenanceRequestDTO> requests = maintenanceRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{requestId}/mark-not-fixed")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> markAsNotFixed(@PathVariable Long requestId) {
        MaintenanceRequestDTO updatedRequest = maintenanceRequestService.markAsNotFixed(requestId);
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/owner/me")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsForAuthenticatedOwner(Authentication auth) {
        String email = auth.getName();
        List<MaintenanceRequestDTO> requests = maintenanceRequestService.getRequestsByOwnerEmail(email);
        return ResponseEntity.ok(requests);
    }

    @PostMapping(value = "/lease/{leaseId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MaintenanceRequestDTO> createRequest(
            @PathVariable Long leaseId,
           @RequestPart("requestDTO") MaintenanceRequestDTO requestDTO,
           @RequestPart (value = "images", required = false ) List<MultipartFile> images) throws IOException {

        MaintenanceRequestDTO createdRequest = maintenanceRequestService.createRequest(leaseId, requestDTO,images);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsForLease(
            @PathVariable Long leaseId) {

        List<MaintenanceRequestDTO> requests = maintenanceRequestService.getRequestsByLease(leaseId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsByPropertyOwner(
            @PathVariable Long ownerId) {
        List<MaintenanceRequestDTO> requests = maintenanceRequestService.getRequestsByPropertyOwner(ownerId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{requestId}/status")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MaintenanceRequestDTO> updateStatus(
            @PathVariable Long requestId,
            @RequestParam MaintenanceStatus status) {

        MaintenanceRequestDTO updatedRequest = maintenanceRequestService.setStatus(requestId, status);
        return ResponseEntity.ok(updatedRequest);
    }

    @PatchMapping("/{requestId}/cost")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MaintenanceRequestDTO> setCost(
            @PathVariable Long requestId,
            @RequestParam BigDecimal cost) {

        MaintenanceRequestDTO updated = maintenanceRequestService.setCost(requestId, cost);
        return ResponseEntity.ok(updated);
    }

}
