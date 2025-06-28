package com.immobile.real_estate_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.dto.LeaseRequestDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.LeaseTerminationStatus;
import com.immobile.real_estate_backend.service.LeaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lease")
@AllArgsConstructor
@Slf4j
public class LeaseController {

    private final LeaseService leaseService;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<LeaseDTO> getMyLease(Authentication authentication) {
        String email = authentication.getName();
        LeaseDTO lease = leaseService.getLeaseByTenantEmail(email);
        System.out.println(lease);
        return ResponseEntity.ok(lease);
    }

    @GetMapping("/termination-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaseDTO>> getPendingTerminationRequests() {
        List<LeaseDTO> pendingLeases = leaseService.getPendingTerminations();
        return ResponseEntity.ok(pendingLeases);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaseDTO>> getPendingLeases() {
        List<LeaseDTO> pendingLeases = leaseService.findPendingLeases();
        return ResponseEntity.ok(pendingLeases);
    }

    @PatchMapping("/{leaseId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaseDTO> approveLease(@PathVariable Long leaseId) {
        LeaseDTO updatedLease = leaseService.updateLeaseStatus(leaseId, LeaseStatus.ACTIVE);
        return ResponseEntity.ok(updatedLease);
    }

    @PatchMapping("/{leaseId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaseDTO> rejectLease(@PathVariable Long leaseId) {
        LeaseDTO updatedLease = leaseService.updateLeaseStatus(leaseId, LeaseStatus.TERMINATED);
        return ResponseEntity.ok(updatedLease);
    }

    @GetMapping("/owner/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getLeasesByOwner(Authentication auth) {
        String email = auth.getName();
        List<LeaseDTO> leases = leaseService.findLeasesByOwnerEmail(email);
        return ResponseEntity.ok(leases);
    }

    @PostMapping("/{leaseId}/terminate-request")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> requestLeaseTermination(@PathVariable Long leaseId) {
        leaseService.requestTermination(leaseId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{leaseId}/terminate-decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaseDTO> decideTermination(
            @PathVariable Long leaseId,
            @RequestParam LeaseTerminationStatus decision) {

        LeaseDTO updated = leaseService.handleTerminationDecision(leaseId, decision);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<String> createInvitation(@RequestBody LeaseRequestDTO request) {
        leaseService.sendLeaseInvitationOnly(request);
        return ResponseEntity.ok("Invitation sent successfully.");
    }

    @GetMapping("/{leaseId}")
    public ResponseEntity<LeaseDTO> getLease(@PathVariable("leaseId") Long leaseId) {
        return new ResponseEntity<>(leaseService.getLeaseDTO(leaseId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LeaseDTO>> getAllLeases() {
        return new ResponseEntity<>(leaseService.getAllLeasesDTO(), HttpStatus.OK);
    }

    @DeleteMapping("/{leaseId}")
    public void deleteLease(@PathVariable("leaseId") Long leaseId) {
        leaseService.deleteLeaseById(leaseId);
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<List<LeaseDTO>> getAllActiveLeasesById(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(leaseService.getActiveLeasesDTO(userId), HttpStatus.OK);
    }

    @GetMapping("/dashboard/lease-trends")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaseDTO>> getLeaseTrendsForAdmin() {
        return ResponseEntity.ok(leaseService.getLeaseTrends());
    }

    @GetMapping("/tenant/active")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<LeaseDTO> getTenantActiveLease(Authentication authentication) {
        try {
            String email = authentication.getName();

            LeaseDTO activeLease = leaseService.getLeaseByTenantEmail(email);

            if (activeLease == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(activeLease);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/tenant/exists")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Boolean> checkTenantHasLease(Authentication authentication) {
        try {
            String email = authentication.getName();

            boolean hasLease = leaseService.tenantHasAnyLease(email);

            return ResponseEntity.ok(hasLease);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    @GetMapping("/tenant/all")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> getAllTenantLeases(Authentication authentication) {
        try {
            String email = authentication.getName();

            var leases = leaseService.getAllLeasesByTenantEmail(email);

            return ResponseEntity.ok(leases);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch leases"));
        }
    }

    @GetMapping("/tenant/terminated")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> getTerminatedLeases(Authentication authentication) {
        try {
            String email = authentication.getName();

            var terminatedLeases = leaseService.getTerminatedLeasesByTenantEmail(email);

            return ResponseEntity.ok(terminatedLeases);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch terminated leases"));
        }
    }

}