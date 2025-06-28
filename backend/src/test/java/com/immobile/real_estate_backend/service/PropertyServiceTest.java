package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.PropertyConverter;
import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.entity.ActivityLog;
import com.immobile.real_estate_backend.model.entity.Property;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.PropertyType;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import com.immobile.real_estate_backend.repository.ActivityLogRepository;
import com.immobile.real_estate_backend.repository.PropertyRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyConverter propertyConverter;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PropertyService propertyService;

    private User testUser;
    private Property testProperty;
    private PropertyDTO testPropertyDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testProperty = Property.builder()
                .propertyId(1L)
                .owner(testUser)
                .name("Test Property")
                .address("123 Test Street")
                .type(PropertyType.APARTMENT)
                .rentAmount(BigDecimal.valueOf(1200.00))
                .status(PropertyStatus.AVAILABLE)
                .validationStatus(ValidationStatus.PENDING)
                .images(new ArrayList<>())
                .build();

        testPropertyDTO = PropertyDTO.builder()
                .propertyId(1L)
                .ownerId(1L)
                .name("Test Property")
                .address("123 Test Street")
                .type(PropertyType.APARTMENT)
                .rentAmount(BigDecimal.valueOf(1200.00))
                .status(PropertyStatus.AVAILABLE)
                .validationStatus(ValidationStatus.PENDING)
                .build();
    }

    @Test
    void createProperty_ShouldCreatePropertySuccessfully() throws IOException {
        List<MultipartFile> images = List.of(
                new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test image 1".getBytes()),
                new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "test image 2".getBytes())
        );

        when(propertyConverter.toPropertyForCreation(testPropertyDTO)).thenReturn(testProperty);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(propertyConverter.toPropertyDTO(testProperty)).thenReturn(testPropertyDTO);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());

        PropertyDTO result = propertyService.createProperty(testPropertyDTO, images);

        assertNotNull(result);
        assertEquals(testPropertyDTO.getName(), result.getName());
        assertEquals(testPropertyDTO.getAddress(), result.getAddress());
        verify(propertyRepository).save(any(Property.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void createProperty_ShouldCreatePropertyWithoutImages() throws IOException {
        when(propertyConverter.toPropertyForCreation(testPropertyDTO)).thenReturn(testProperty);
        when(propertyRepository.save(testProperty)).thenReturn(testProperty);
        when(propertyConverter.toPropertyDTO(testProperty)).thenReturn(testPropertyDTO);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());

        PropertyDTO result = propertyService.createProperty(testPropertyDTO, null);

        assertNotNull(result);
        assertEquals(testPropertyDTO.getName(), result.getName());
        verify(propertyRepository).save(testProperty);
    }

    @Test
    void getProperty_ShouldReturnProperty_WhenPropertyExists() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        Property result = propertyService.getProperty(1L);

        assertNotNull(result);
        assertEquals(testProperty.getName(), result.getName());
        verify(propertyRepository).findById(1L);
    }

    @Test
    void getProperty_ShouldReturnNull_WhenPropertyNotExists() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        Property result = propertyService.getProperty(999L);

        assertNull(result);
        verify(propertyRepository).findById(999L);
    }

    @Test
    void findByUserEmail_ShouldReturnProperties_WhenUserExists() {
        List<Property> properties = List.of(testProperty);
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(propertyRepository.getPropertiesByOwner(testUser)).thenReturn(properties);
        when(propertyConverter.toPropertyDTO(testProperty)).thenReturn(testPropertyDTO);

        List<PropertyDTO> result = propertyService.findByUserEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPropertyDTO.getName(), result.get(0).getName());
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(propertyRepository).getPropertiesByOwner(testUser);
    }

    @Test
    void findByUserEmail_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> propertyService.findByUserEmail("nonexistent@example.com"));
    }

    @Test
    void validateProperty_ShouldApproveProperty_WhenStatusIsApproved() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.validateProperty(1L, ValidationStatus.APPROVED);

        verify(propertyRepository).save(argThat(property ->
                property.getValidationStatus() == ValidationStatus.APPROVED &&
                        property.getStatus() == PropertyStatus.AVAILABLE));
    }

    @Test
    void validateProperty_ShouldRejectProperty_WhenStatusIsRejected() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.validateProperty(1L, ValidationStatus.REJECTED);

        verify(propertyRepository).save(argThat(property ->
                property.getValidationStatus() == ValidationStatus.REJECTED &&
                        property.getStatus() == PropertyStatus.INACTIVE &&
                        property.getIsFlagged()));
    }

    @Test
    void validateProperty_ShouldThrowException_WhenPropertyNotExists() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> propertyService.validateProperty(999L, ValidationStatus.APPROVED));
    }

    @Test
    void updatePropertyStatus_ShouldUpdateStatus_WhenPropertyExists() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        propertyService.updatePropertyStatus(1L, PropertyStatus.RENTED);

        verify(propertyRepository).save(argThat(property ->
                property.getStatus() == PropertyStatus.RENTED));
    }

    @Test
    void updatePropertyStatus_ShouldThrowException_WhenPropertyNotExists() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> propertyService.updatePropertyStatus(999L, PropertyStatus.RENTED));
    }

    @Test
    void deletePropertyById_ShouldDeleteProperty_WhenPropertyExists() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());
        doNothing().when(propertyRepository).deleteById(1L);

        propertyService.deletePropertyById(1L);

        verify(propertyRepository).deleteById(1L);
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void deletePropertyById_ShouldThrowException_WhenPropertyNotExists() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class,
                () -> propertyService.deletePropertyById(999L));
    }

    @Test
    void findPendingProperties_ShouldReturnPendingProperties() {
        List<Property> pendingProperties = List.of(testProperty);
        when(propertyRepository.findByValidationStatus(ValidationStatus.PENDING))
                .thenReturn(pendingProperties);
        when(propertyConverter.toPropertyDTO(testProperty)).thenReturn(testPropertyDTO);

        List<PropertyDTO> result = propertyService.findPendingProperties();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPropertyDTO.getName(), result.get(0).getName());
        verify(propertyRepository).findByValidationStatus(ValidationStatus.PENDING);
    }

    @Test
    void getAllProperties_ShouldReturnAllProperties() {
        List<Property> properties = List.of(testProperty);
        when(propertyRepository.findAll()).thenReturn(properties);

        List<Property> result = propertyService.getAllProperties();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProperty.getName(), result.get(0).getName());
        verify(propertyRepository).findAll();
    }

    @Test
    void getPropertyDTO_ShouldReturnPropertyDTO_WhenPropertyExists() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyConverter.toPropertyDTO(testProperty)).thenReturn(testPropertyDTO);

        PropertyDTO result = propertyService.getPropertyDTO(1L);

        assertNotNull(result);
        assertEquals(testPropertyDTO.getName(), result.getName());
        verify(propertyRepository).findById(1L);
        verify(propertyConverter).toPropertyDTO(testProperty);
    }

    @Test
    void getPropertyDTO_ShouldReturnNull_WhenPropertyNotExists() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        PropertyDTO result = propertyService.getPropertyDTO(999L);

        assertNull(result);
        verify(propertyRepository).findById(999L);
    }
}
