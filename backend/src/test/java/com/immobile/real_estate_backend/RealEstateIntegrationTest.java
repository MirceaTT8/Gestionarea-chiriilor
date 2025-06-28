package com.immobile.real_estate_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.dto.PropertyDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.dto.auth.AuthRequest;
import com.immobile.real_estate_backend.model.dto.auth.RegisterRequest;
import com.immobile.real_estate_backend.model.entity.Role;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.model.enums.PropertyType;
import com.immobile.real_estate_backend.model.enums.ValidationStatus;
import com.immobile.real_estate_backend.repository.RoleRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class RealEstateIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private RegisterRequest landlordRegisterRequest;
    private RegisterRequest tenantRegisterRequest;
    private PropertyDTO propertyDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        createRoleIfNotExists("LANDLORD");
        createRoleIfNotExists("TENANT");
        createRoleIfNotExists("ADMIN");

        landlordRegisterRequest = new RegisterRequest();
        landlordRegisterRequest.setFirstName("John");
        landlordRegisterRequest.setLastName("Landlord");
        landlordRegisterRequest.setEmail("landlord@test.com");
        landlordRegisterRequest.setPassword("password123");

        tenantRegisterRequest = new RegisterRequest();
        tenantRegisterRequest.setFirstName("Jane");
        tenantRegisterRequest.setLastName("Tenant");
        tenantRegisterRequest.setEmail("tenant@test.com");
        tenantRegisterRequest.setPassword("password123");

        propertyDTO = PropertyDTO.builder()
                .name("Integration Test Property")
                .address("123 Integration Test Street")
                .type(PropertyType.APARTMENT)
                .rentAmount(BigDecimal.valueOf(1500.00))
                .latitude(45.7489)
                .longitude(21.2087)
                .build();
    }

    private void createRoleIfNotExists(String roleName) {
        Role existingRole = roleRepository.findRoleByName(roleName);
        if (existingRole == null) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
        }
    }

    @Test
    void completeUserRegistrationAndAuthenticationFlow() throws Exception {
        // Step 1: Register a new tenant user
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenantRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(registerResponse).get("token").asText();

        // Step 2: Use the token to access protected endpoint
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tenant@test.com"));

        // Step 3: Login with the same credentials
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("tenant@test.com");
        authRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void completePropertyManagementFlow() throws Exception {
        // Step 1: Create users
        User landlord = createLandlordUser();
        String landlordToken = createTokenForUser(landlord);

        User admin = createAdminUser();
        String adminToken = createTokenForUser(admin);

        User tenant = createTenantUser();
        String tenantToken = createTokenForUser(tenant);

        // Step 2: Create property
        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "property.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(propertyDTO).getBytes()
        );

        MvcResult createPropertyResult = mockMvc.perform(multipart("/property")
                        .file(propertyData)
                        .header("Authorization", "Bearer " + landlordToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Property"))
                .andExpect(jsonPath("$.validationStatus").value("PENDING"))
                .andReturn();

        String propertyResponse = createPropertyResult.getResponse().getContentAsString();
        PropertyDTO createdProperty = objectMapper.readValue(propertyResponse, PropertyDTO.class);
        Long propertyId = createdProperty.getPropertyId();

        // Step 3: Get landlord's properties
        mockMvc.perform(get("/property/user/me")
                        .header("Authorization", "Bearer " + landlordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Integration Test Property"));

        // Step 4: Admin approves property
        mockMvc.perform(patch("/property/" + propertyId + "/validate")
                        .param("status", "APPROVED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Validation status updated to APPROVED"));

        // Step 5: Verify property is available (test with different user roles)

        // Test 1: Admin can view the property
        mockMvc.perform(get("/property/" + propertyId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationStatus").value(ValidationStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.status").value(PropertyStatus.AVAILABLE.toString()));

        // Test 2: Landlord can view their own property
        mockMvc.perform(get("/property/" + propertyId)
                        .header("Authorization", "Bearer " + landlordToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationStatus").value(ValidationStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.status").value(PropertyStatus.AVAILABLE.toString()));

        // Test 3: Tenant can view available properties
        mockMvc.perform(get("/property/" + propertyId)
                        .header("Authorization", "Bearer " + tenantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationStatus").value(ValidationStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.status").value(PropertyStatus.AVAILABLE.toString()));
    }

    @Test
    void unauthorizedAccessShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/property/user/me"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/property")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tenantShouldNotAccessLandlordEndpoints() throws Exception {
        User tenant = createTenantUser();
        String tenantToken = createTokenForUser(tenant);

        // Test 1: Get landlord properties (this should work fine)
        mockMvc.perform(get("/property/user/me")
                        .header("Authorization", "Bearer " + tenantToken))
                .andExpect(status().isForbidden());

        // Test 2: Create property - FIX THE MULTIPART FORMAT
        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "property.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(propertyDTO).getBytes()
        );

        mockMvc.perform(multipart("/property")
                        .file(propertyData)  // Use MockMultipartFile instead of .file(String, byte[])
                        .header("Authorization", "Bearer " + tenantToken))
                .andExpect(status().isForbidden()); // Now you should get 403 instead of 415
    }

    @Test
    void adminCanAccessAllEndpoints() throws Exception {
        User admin = createAdminUser();
        String adminToken = createTokenForUser(admin);

        User landlord = createLandlordUser();
        String landlordToken = createTokenForUser(landlord);

        MockMultipartFile propertyData = new MockMultipartFile(
                "propertyDTO",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(propertyDTO).getBytes()
        );

        MvcResult createResult = mockMvc.perform(multipart("/property")
                        .file(propertyData)
                        .header("Authorization", "Bearer " + landlordToken))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String propertyResponse = createResult.getResponse().getContentAsString();
        PropertyDTO createdProperty = objectMapper.readValue(propertyResponse, PropertyDTO.class);

        // Admin should be able to access admin endpoints
        mockMvc.perform(get("/property/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(patch("/property/" + createdProperty.getPropertyId() + "/validate")
                        .param("status", ValidationStatus.APPROVED.toString())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/property/" + createdProperty.getPropertyId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    private User createLandlordUser() {
        Role landlordRole = roleRepository.findRoleByName("LANDLORD");
        User landlord = User.builder()
                .firstName("Test")
                .lastName("Landlord")
                .email("test.landlord@test.com")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .roles(List.of(landlordRole))
                .build();
        return userRepository.save(landlord);
    }

    private User createTenantUser() {
        Role tenantRole = roleRepository.findRoleByName("TENANT");
        User tenant = User.builder()
                .firstName("Test")
                .lastName("Tenant")
                .email("test.tenant@test.com")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .roles(List.of(tenantRole))
                .build();
        return userRepository.save(tenant);
    }

    private User createAdminUser() {
        Role adminRole = roleRepository.findRoleByName("ADMIN");
        User admin = User.builder()
                .firstName("Test")
                .lastName("Admin")
                .email("test.admin@test.com")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .roles(List.of(adminRole))
                .build();
        return userRepository.save(admin);
    }

    private String createTokenForUser(User user) throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
