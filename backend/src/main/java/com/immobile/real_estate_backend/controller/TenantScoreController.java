package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.TenantStatsDTO;
import com.immobile.real_estate_backend.model.entity.TenantStats;
import com.immobile.real_estate_backend.service.TenantScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tenant-score")
public class TenantScoreController {

    private final TenantScoreService tenantScoreService;

    @GetMapping("/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<Double> getTenantScore(@PathVariable Long tenantId) {
        double score = tenantScoreService.calculateTenantScore(tenantId);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/punctuality/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<Double> getPunctualityScore(@PathVariable Long tenantId) {
        double punctualityScore = tenantScoreService.getPaymentPunctualityScore(tenantId);
        return ResponseEntity.ok(punctualityScore);
    }

    @GetMapping("/feedback/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<Double> getFeedbackScore(@PathVariable Long tenantId) {
        double feedbackScore = tenantScoreService.getFeedbackScore(tenantId);
        return ResponseEntity.ok(feedbackScore);
    }

    @GetMapping("/{tenantId}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<TenantStatsDTO> getTenantStats(@PathVariable Long tenantId) {
        TenantStatsDTO stats = tenantScoreService.getTenantStats(tenantId);
        if (stats == null) {
            updateTenantScore(tenantId);
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TenantStatsDTO>> getAllTenantStats() {
        List<TenantStatsDTO> stats = tenantScoreService.getAllTenantStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/update/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateTenantScore(@PathVariable Long tenantId) {
        tenantScoreService.updateScoreForTenant(tenantId);
        return ResponseEntity.ok().build();
    }
}