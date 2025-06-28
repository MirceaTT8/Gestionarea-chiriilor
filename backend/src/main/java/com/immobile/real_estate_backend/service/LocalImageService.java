package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.entity.Image;
import com.immobile.real_estate_backend.repository.ImageRepository;
import com.immobile.real_estate_backend.repository.PropertyRepository;
import com.immobile.real_estate_backend.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalImageService {

    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    @Transactional
    public void loadDemoImagesFromLocal() {
        Map<String, DemoImageInfo> imageFiles = createLocalImageMappings();

        for (Map.Entry<String, DemoImageInfo> entry : imageFiles.entrySet()) {
            try {
                String fileName = entry.getKey();
                DemoImageInfo info = entry.getValue();

                byte[] imageBytes = loadImageFromResources(info.resourcePath);

                if (imageBytes != null) {
                    Image image = Image.builder()
                            .name(fileName)
                            .contentType("image/jpeg")
                            .content(imageBytes)
                            .build();

                    if (info.propertyId != null) {
                        propertyRepository.findById(info.propertyId)
                                .ifPresent(image::setProperty);
                    }

                    if (info.maintenanceId != null) {
                        maintenanceRequestRepository.findById(info.maintenanceId)
                                .ifPresent(image::setMaintenanceRequest);
                    }

                    imageRepository.save(image);
                    log.info("Loaded local demo image: {}", fileName);
                } else {
                    log.warn("Failed to load image: {}", fileName);
                }

            } catch (Exception e) {
                log.error("Error loading image: {}", entry.getKey(), e);
            }
        }
    }

    private Map<String, DemoImageInfo> createLocalImageMappings() {
        Map<String, DemoImageInfo> imageFiles = new HashMap<>();

        imageFiles.put("studio_exterior.jpg", new DemoImageInfo("demo/properties/prop2_img1.png", 3L, null));
        imageFiles.put("studio_living.jpg", new DemoImageInfo("demo/properties/prop2_img2.png", 3L, null));
        imageFiles.put("studio_kitchen.jpg", new DemoImageInfo("demo/properties/prop2_img3.png", 3L, null));
        imageFiles.put("studio_bathroom.jpg", new DemoImageInfo("demo/properties/prop2_img4.png", 3L, null));

        imageFiles.put("penthouse_exterior.jpg", new DemoImageInfo("demo/properties/prop3_img1.png", 4L, null));
        imageFiles.put("penthouse_living.jpg", new DemoImageInfo("demo/properties/prop3_img2.png", 4L, null));
        imageFiles.put("penthouse_kitchen.jpg", new DemoImageInfo("demo/properties/prop3_img3.png", 4L, null));
        imageFiles.put("penthouse_bedroom.jpg", new DemoImageInfo("demo/properties/prop3_img4.png", 4L, null));

        imageFiles.put("studio_exterior.jpg", new DemoImageInfo("demo/properties/prop2_img1.png", 1L, null));
        imageFiles.put("studio_living.jpg", new DemoImageInfo("demo/properties/prop2_img2.png", 1L, null));
        imageFiles.put("studio_kitchen.jpg", new DemoImageInfo("demo/properties/prop2_img3.png", 1L, null));
        imageFiles.put("studio_bathroom.jpg", new DemoImageInfo("demo/properties/prop2_img4.png", 1L, null));

        imageFiles.put("penthouse_exterior.jpg", new DemoImageInfo("demo/properties/prop3_img1.png", 2L, null));
        imageFiles.put("penthouse_living.jpg", new DemoImageInfo("demo/properties/prop3_img2.png", 2L, null));
        imageFiles.put("penthouse_kitchen.jpg", new DemoImageInfo("demo/properties/prop3_img3.png", 2L, null));
        imageFiles.put("penthouse_bedroom.jpg", new DemoImageInfo("demo/properties/prop3_img4.png", 2L, null));

        imageFiles.put("ac_problem.jpg", new DemoImageInfo("demo/maintenance/ac_problem.webp", null, 3L));
        imageFiles.put("balcony_door_issue.jpg", new DemoImageInfo("demo/maintenance/door_issue.jpg", null, 4L));
        imageFiles.put("window_problem.jpg", new DemoImageInfo("demo/maintenance/window_problem.jpg", null, 6L));
        imageFiles.put("electrical_outlet.jpg", new DemoImageInfo("demo/maintenance/electrical_issue.jpg", null, 8L));

        imageFiles.put("dripping_kitchen_faucet.jpg", new DemoImageInfo("demo/maintenance/leaking-faucet.jpg", null, 1L));
        imageFiles.put("broken_light_switch.jpg", new DemoImageInfo("demo/maintenance/broken-light-switch.jpg", null, 2L));

        return imageFiles;
    }

    private byte[] loadImageFromResources(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    return inputStream.readAllBytes();
                }
            } else {
                log.warn("Image resource not found: {}", resourcePath);
                return null;
            }
        } catch (IOException e) {
            log.error("Error loading image from resources: {}", resourcePath, e);
            return null;
        }
    }

    private static class DemoImageInfo {
        final String resourcePath;
        final Long propertyId;
        final Long maintenanceId;

        DemoImageInfo(String resourcePath, Long propertyId, Long maintenanceId) {
            this.resourcePath = resourcePath;
            this.propertyId = propertyId;
            this.maintenanceId = maintenanceId;
        }
    }
}
