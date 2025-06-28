package com.immobile.real_estate_backend.config;

import com.immobile.real_estate_backend.repository.ImageRepository;
import com.immobile.real_estate_backend.service.LocalImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"demo"})
public class StartUpDemoLoader implements CommandLineRunner {

    private final LocalImageService localImageService;
    private final ImageRepository imageRepository;

    @Override
    public void run(String... args) {
        log.info("Checking if demo images need to be loaded...");

        long imageCount = imageRepository.count();

        if (imageCount == 0) {
            log.info("No images found in database. Loading demo images...");
            try {
                localImageService.loadDemoImagesFromLocal();

                log.info("Demo images loaded successfully at startup!");
            } catch (Exception e) {
                log.error("Failed to load demo images at startup", e);
            }
        } else {
            log.info("Found {} images in database. Skipping demo image loading.", imageCount);
        }
    }
}