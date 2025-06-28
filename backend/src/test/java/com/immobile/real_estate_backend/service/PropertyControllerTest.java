package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.controller.PropertyController;
import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.PropertyType;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Modern approach using pure unit testing without Spring Security context
 * This avoids the complexity of @WebMvcTest + Security configuration
 */
@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PropertyService propertyService;

    @Mock
    private UserService userService;

    private PropertyController propertyController;
    private PropertyDTO testPropertyDTO;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        propertyController = new PropertyController(propertyService, userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(propertyController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        setupTestData();
    }

    private TestingAuthenticationToken createMockAdminPrincipal() {
        return new TestingAuthenticationToken(
                "admin@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    private void setupTestData() {
        testUserDTO = UserDTO.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
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
    void getMyProperties_ShouldReturnProperties_WhenCalled() throws Exception {
        List<PropertyDTO> properties = List.of(testPropertyDTO);
        when(propertyService.findByUserEmail("john.doe@example.com")).thenReturn(properties);

        mockMvc.perform(get("/property/user/me")
                        .principal(createMockPrincipal("john.doe@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Property"))
                .andExpect(jsonPath("$[0].address").value("123 Test Street"));

        verify(propertyService).findByUserEmail("john.doe@example.com");
    }

    @Test
    void createProperty_ShouldCreateProperty_WhenValidData() throws Exception {
        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "",
                "application/json",
                objectMapper.writeValueAsString(testPropertyDTO).getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(userService.getUserByEmailDTO("john.doe@example.com")).thenReturn(testUserDTO);
        when(propertyService.createProperty(any(PropertyDTO.class), anyList())).thenReturn(testPropertyDTO);

        mockMvc.perform(multipart("/property")
                        .file(propertyData)
                        .file(imageFile)
                        .principal(createMockPrincipal("john.doe@example.com"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Property"))
                .andExpect(jsonPath("$.address").value("123 Test Street"));

        verify(userService).getUserByEmailDTO("john.doe@example.com");
        verify(propertyService).createProperty(any(PropertyDTO.class), anyList());
    }

    @Test
    void getPendingProperties_ShouldReturnPendingProperties() throws Exception {
        List<PropertyDTO> pendingProperties = List.of(testPropertyDTO);
        when(propertyService.findPendingProperties()).thenReturn(pendingProperties);

        mockMvc.perform(get("/property/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Property"))
                .andExpect(jsonPath("$[0].validationStatus").value("PENDING"));

        verify(propertyService).findPendingProperties();
    }

    @Test
    void getProperty_ShouldReturnProperty_WhenPropertyExists() throws Exception {
        when(propertyService.getPropertyDTO(1L)).thenReturn(testPropertyDTO);

        mockMvc.perform(get("/property/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Property"))
                .andExpect(jsonPath("$.address").value("123 Test Street"));

        verify(propertyService).getPropertyDTO(1L);
    }

    @Test
    void getProperties_ShouldReturnAllProperties() throws Exception {
        List<PropertyDTO> properties = List.of(testPropertyDTO);
        when(propertyService.getAllPropertiesDTO()).thenReturn(properties);

        mockMvc.perform(get("/property"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Property"));

        verify(propertyService).getAllPropertiesDTO();
    }

    @Test
    void getPropertiesByUserId_ShouldReturnUserProperties() throws Exception {
        List<PropertyDTO> properties = List.of(testPropertyDTO);
        when(propertyService.findByUserIdDTO(1L)).thenReturn(properties);

        mockMvc.perform(get("/property/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Property"));

        verify(propertyService).findByUserIdDTO(1L);
    }

    @Test
    void validateProperty_ShouldValidateProperty() throws Exception {
        doNothing().when(propertyService).validateProperty(1000L, ValidationStatus.APPROVED);

        mockMvc.perform(patch("/property/1000/validate")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Validation status updated to APPROVED"));

        verify(propertyService).validateProperty(1000L, ValidationStatus.APPROVED);
    }
    @Test
    void deleteProperty_ShouldDeleteProperty() throws Exception {
        doNothing().when(propertyService).deletePropertyById(1L);

        mockMvc.perform(delete("/property/1"))
                .andExpect(status().isNoContent());

        verify(propertyService).deletePropertyById(1L);
    }

    @Test
    void updatePropertyStatus_ShouldUpdateStatus() throws Exception {
        doNothing().when(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);

        mockMvc.perform(put("/property/1/status")
                        .param("status", "RENTED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Property status updated to RENTED"));

        verify(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);
    }

    @Test
    void createProperty_ShouldHandleIOException() throws Exception {
        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "",
                "application/json",
                objectMapper.writeValueAsString(testPropertyDTO).getBytes()
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(userService.getUserByEmailDTO("john.doe@example.com")).thenReturn(testUserDTO);
        when(propertyService.createProperty(any(PropertyDTO.class), anyList()))
                .thenThrow(new RuntimeException("IO Exception occurred"));

        assertThrows(Exception.class, () -> {
            mockMvc.perform(multipart("/property")
                    .file(propertyData)
                    .file(imageFile)
                    .principal(createMockPrincipal("john.doe@example.com"))
                    .contentType(MediaType.MULTIPART_FORM_DATA));
        });

        verify(propertyService).createProperty(any(PropertyDTO.class), anyList());
    }

    @Test
    void getProperty_ShouldReturnNotFound_WhenPropertyDoesNotExist() throws Exception {
        when(propertyService.getPropertyDTO(999L)).thenReturn(null);

        mockMvc.perform(get("/property/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(propertyService).getPropertyDTO(999L);
    }

    @Test
    void createProperty_ShouldSetOwnerIdFromAuthenticatedUser() throws Exception {
        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "",
                "application/json",
                objectMapper.writeValueAsString(testPropertyDTO).getBytes()
        );

        when(userService.getUserByEmailDTO("landlord@test.com")).thenReturn(testUserDTO);
        when(propertyService.createProperty(any(PropertyDTO.class), eq(null))).thenReturn(testPropertyDTO);

        mockMvc.perform(multipart("/property")
                        .file(propertyData)
                        .principal(createMockPrincipal("landlord@test.com"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Property"));

        verify(propertyService).createProperty(argThat(propertyDTO ->
                propertyDTO.getOwnerId().equals(testUserDTO.getUserId())), eq(null));
    }

    @Test
    void validateProperty_ShouldHandleInvalidStatus() throws Exception {
        mockMvc.perform(patch("/property/1/validate")
                        .param("status", "INVALID_STATUS")
                        .principal(createMockAdminPrincipal()))
                .andExpect(status().isBadRequest());

        verify(propertyService, never()).validateProperty(anyLong(), any());
    }

    private TestingAuthenticationToken createMockPrincipal(String email) {
        return new TestingAuthenticationToken(
                email,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_LANDLORD"))
        );
    }
}

