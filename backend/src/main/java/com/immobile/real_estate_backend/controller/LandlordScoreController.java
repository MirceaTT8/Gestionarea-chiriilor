package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.service.LandlordScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/landlord-score")
public class LandlordScoreController {

    private final LandlordScoreService landlordScoreService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLandlordScores() {
        return ResponseEntity.ok(landlordScoreService.getAllScores());
    }

    @GetMapping("/{landlordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getScore(@PathVariable Long landlordId) {
        return ResponseEntity.ok(landlordScoreService.calculateLandlordScore(landlordId));
    }

    @GetMapping("/rating/{landlordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getRatingScore(@PathVariable Long landlordId) {
        return ResponseEntity.ok(landlordScoreService.getRatingScore(landlordId));
    }

    @GetMapping("/maintenance/{landlordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getMaintenanceScore(@PathVariable Long landlordId) {
        return ResponseEntity.ok(landlordScoreService.getMaintenanceScore(landlordId));
    }

    @GetMapping("/behavior/{landlordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getBehaviorScore(@PathVariable Long landlordId) {
        return ResponseEntity.ok(landlordScoreService.getAppBehaviorScore(landlordId));
    }

    @GetMapping("/response-time/{landlordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getAvgResponseTime(@PathVariable Long landlordId) {
        return ResponseEntity.ok(landlordScoreService.calculateAvgResponseTime(landlordId));
    }
}
