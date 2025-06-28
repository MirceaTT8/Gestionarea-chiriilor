package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.PropertyConverter;
import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import com.immobile.real_estate_backend.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyConverter propertyConverter;
    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO, List<MultipartFile> images) throws IOException {
        Property property = propertyConverter.toPropertyForCreation(propertyDTO);

        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    Image image = Image.builder()
                            .name(file.getOriginalFilename())
                            .content(file.getBytes())
                            .contentType(file.getContentType())
                            .property(property)
                            .build();
                    property.addImage(image);
                }
            }
        }

        Property savedProperty = propertyRepository.save(property);

        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .user(savedProperty.getOwner())
                    .actionType("CREATE_PROPERTY")
                    .entityType("Property")
                    .entityId(savedProperty.getPropertyId())
                    .details(objectMapper.writeValueAsString(
                            propertyConverter.toPropertyDTO(savedProperty)))
                    .build();

            activityLogRepository.save(logEntry);
            log.info("Property created with ID: {} at address: {} with {} images",
                    savedProperty.getPropertyId(),
                    savedProperty.getAddress(),
                    savedProperty.getImages().size());

        } catch (JsonProcessingException e) {
            log.error("Failed to log property creation: {}", e.getMessage());
        }

        return propertyConverter.toPropertyDTO(savedProperty);
    }

    public Property getProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property == null) {
            log.warn("Property not found with ID: {}", id);
        } else {
            log.debug("Retrieved property with ID: {}", id);
        }
        return property;
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        List<Property> properties = propertyRepository.getPropertiesByOwner(ownerId);
        log.debug("Retrieved {} properties for owner ID: {}", properties.size(), ownerId);
        return properties;
    }

    public List<PropertyDTO> findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return propertyRepository.getPropertiesByOwner(user).stream()
                .map(propertyConverter::toPropertyDTO)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> findByUserIdDTO(Long ownerId) {
        List<PropertyDTO> properties = getPropertiesByOwner(ownerId).stream()
                .map(propertyConverter::toPropertyDTO)
                .collect(Collectors.toList());

        log.debug("Retrieved {} property DTOs for owner ID: {}", properties.size(), ownerId);
        return properties;
    }

    public List<Property> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        log.debug("Retrieved all {} properties from system", properties.size());
        return properties;
    }

    public List<PropertyDTO> getAllPropertiesDTO() {
        List<PropertyDTO> properties = getAllProperties().stream()
                .map(propertyConverter::toPropertyDTO)
                .collect(Collectors.toList());

        log.debug("Retrieved all {} property DTOs from system", properties.size());
        return properties;
    }

    public PropertyDTO getPropertyDTO(Long id) {
        Property property = getProperty(id);
        if (property == null) {
            return null;
        }
        log.debug("Retrieved property DTO with ID: {}", id);
        return propertyConverter.toPropertyDTO(property);
    }

    @Transactional
    public void deletePropertyById(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            try {
                ActivityLog logEntry = ActivityLog.builder()
                        .user(property.getOwner())
                        .actionType("DELETE_PROPERTY")
                        .entityType("Property")
                        .entityId(id)
                        .details(String.format("Property at %s was deleted", property.getAddress()))
                        .build();

                activityLogRepository.save(logEntry);
                propertyRepository.deleteById(id);
                log.info("Property deleted with ID: {} at address: {}", id, property.getAddress());

            } catch (Exception e) {
                log.error("Failed to log property deletion: {}", e.getMessage());
                throw new RuntimeException("Failed to delete property");
            }
        } else {
            log.warn("Attempted to delete non-existent property with ID: {}", id);
            throw new RuntimeException("Property not found");
        }
    }

    public List<PropertyDTO> findPendingProperties() {
        return propertyRepository.findByValidationStatus(ValidationStatus.PENDING)
                .stream()
                .map(propertyConverter::toPropertyDTO)
                .toList();
    }

    @Transactional
    public void validateProperty(Long propertyId, ValidationStatus validationStatus) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

        property.setValidationStatus(validationStatus);

        if (validationStatus == ValidationStatus.APPROVED) {
            property.setStatus(PropertyStatus.AVAILABLE);
        } if (validationStatus == ValidationStatus.REJECTED) {
            property.setStatus(PropertyStatus.INACTIVE);
            property.setIsFlagged(true);
        }

        propertyRepository.save(property);
        log.info("Property ID {} validation updated to {}", propertyId, validationStatus);
    }

    @Transactional
    public void updatePropertyStatus(Long propertyId, PropertyStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setStatus(status);
        propertyRepository.save(property);
    }

}