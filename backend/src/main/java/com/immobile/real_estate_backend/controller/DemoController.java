package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.service.LocalImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "demo"})
public class DemoController {

    private final LocalImageService localImageService;

//    @PostMapping("/load-demo-images")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, String>> loadDemoImages() {
//        try {
//            log.info("Starting demo images loading...");
//            externalImageService.loadDemoImagesFromUrls();
//
//            return ResponseEntity.ok(Map.of(
//                    "status", "success",
//                    "message", "Demo images loaded successfully!",
//                    "note", "Images downloaded from Unsplash and saved to database"
//            ));
//        } catch (Exception e) {
//            log.error("Error loading demo images", e);
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "error",
//                    "message", "Failed to load demo images: " + e.getMessage()
//            ));
//        }
//    }

    @PostMapping("/load-local-images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> loadLocalImages() {
        try {
            log.info("Starting demo images loading from local resources...");
            localImageService.loadDemoImagesFromLocal();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Demo images loaded successfully from local files!",
                    "note", "Images loaded from src/main/resources/demo/"
            ));
        } catch (Exception e) {
            log.error("Error loading local demo images", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to load local demo images: " + e.getMessage()
            ));
        }
    }
}