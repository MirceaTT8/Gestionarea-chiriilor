package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.PropertyService;
import com.immobile.real_estate_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/property")
@AllArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        return new ResponseEntity<>(propertyService.getAllPropertiesDTO(), HttpStatus.OK);
    }

    @GetMapping("/{propertyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TENANT', 'LANDLORD')")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable("propertyId") Long propertyId) {
        return new ResponseEntity<>(propertyService.getPropertyDTO(propertyId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<PropertyDTO> createProperty(
            @RequestPart(value = "propertyDTO") PropertyDTO propertyDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) throws IOException {

        String email = authentication.getName();

        UserDTO owner = userService.getUserByEmailDTO(email);

        propertyDTO.setOwnerId(owner.getUserId());

        PropertyDTO created = propertyService.createProperty(propertyDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        propertyService.deletePropertyById(propertyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{propertyId}/status")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<String> updatePropertyStatus(
            @PathVariable Long propertyId,
            @RequestParam PropertyStatus status) {

        propertyService.updatePropertyStatus(propertyId, status);
        return ResponseEntity.ok("Property status updated to " + status);
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<List<PropertyDTO>> getMyProperties(Authentication authentication) {
        String email = authentication.getName();
        List<PropertyDTO> properties = propertyService.findByUserEmail(email);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PropertyDTO>> getPendingProperties() {
        List<PropertyDTO> pendingProperties = propertyService.findPendingProperties();
        return ResponseEntity.ok(pendingProperties);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByUserId(@PathVariable("userId") Long ownerId) {
        List<PropertyDTO> properties = propertyService.findByUserIdDTO(ownerId);
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }

    @PatchMapping("/{propertyId}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> validateProperty(
            @PathVariable Long propertyId,
            @RequestParam ValidationStatus status) {

        propertyService.validateProperty(propertyId, status);
        return ResponseEntity.ok("Validation status updated to " + status);
    }

}