package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.entity.Image;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PropertyConverter {
    private final UserRepository userRepository;

    public Property toProperty(PropertyDTO propertyDTO) {
        User owner = userRepository.findById(propertyDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + propertyDTO.getOwnerId()));

        return Property.builder()
                .propertyId(propertyDTO.getPropertyId())
                .owner(owner)
                .name(propertyDTO.getName())
                .address(propertyDTO.getAddress())
                .type(propertyDTO.getType())
                .rentAmount(propertyDTO.getRentAmount())
                .status(propertyDTO.getStatus())
                .latitude(propertyDTO.getLatitude())
                .longitude(propertyDTO.getLongitude())
                .validationStatus(
                        propertyDTO.getValidationStatus() != null
                                ? propertyDTO.getValidationStatus()
                                : ValidationStatus.PENDING
                )
                .isFlagged(Boolean.TRUE.equals(propertyDTO.getIsFlagged()))
                .createdAt(propertyDTO.getCreatedAt())
                .build();
    }

    public Property toPropertyForCreation(PropertyDTO propertyDTO) {
        User owner = userRepository.findById(propertyDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + propertyDTO.getOwnerId()));

        return Property.builder()
                .owner(owner)
                .name(propertyDTO.getName())
                .address(propertyDTO.getAddress())
                .type(propertyDTO.getType())
                .rentAmount(propertyDTO.getRentAmount())
                .status(com.immobile.real_estate_backend.model.enums.PropertyStatus.INACTIVE) // force default
                .validationStatus(ValidationStatus.PENDING) // force default
                .latitude(propertyDTO.getLatitude())
                .longitude(propertyDTO.getLongitude())
                .isFlagged(Boolean.TRUE.equals(propertyDTO.getIsFlagged()))
                .build();
    }


    public PropertyDTO toPropertyDTO(Property property) {
        return PropertyDTO.builder()
                .propertyId(property.getPropertyId())
                .ownerId(property.getOwner().getUserId())
                .name(property.getName())
                .address(property.getAddress())
                .type(property.getType())
                .rentAmount(property.getRentAmount())
                .status(property.getStatus())
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .validationStatus(property.getValidationStatus())
                .isFlagged(property.getIsFlagged())
                .imageUrls(property.getImages().stream()
                        .map(Image::getId)
                        .collect(Collectors.toList()))
                .createdAt(property.getCreatedAt())
                .build();
    }
}