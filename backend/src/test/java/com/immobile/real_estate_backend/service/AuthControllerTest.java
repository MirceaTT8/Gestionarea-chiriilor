package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.config.JwtUtil;
import com.immobile.real_estate_backend.controller.AuthController;
import com.immobile.real_estate_backend.model.converter.AccountInvitationConverter;
import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.model.dto.InviteAcceptanceRequestDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.dto.auth.AuthRequest;
import com.immobile.real_estate_backend.model.dto.auth.RegisterRequest;
import com.immobile.real_estate_backend.model.entity.AccountInvitation;
import com.immobile.real_estate_backend.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private LeaseService leaseService;

    @Mock
    private AccountInvitationService accountInvitationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountInvitationConverter accountInvitationConverter;

    private AuthController authController;
    private AuthRequest authRequest;
    private RegisterRequest registerRequest;
    private InviteAcceptanceRequestDTO inviteRequest;
    private UserDetails userDetails;
    private User testUser;
    private AccountInvitation testInvitation;
    private AccountInvitationDTO testInvitationDTO;

    @BeforeEach
    void setUp() {
        authController = new AuthController(
                authManager,
                userDetailsService,
                jwtUtil,
                userService,
                leaseService,
                accountInvitationService,
                passwordEncoder,
                accountInvitationConverter
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        setupTestData();
    }

    private void setupTestData() {
        authRequest = new AuthRequest();
        authRequest.setEmail("john.doe@example.com");
        authRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");

        inviteRequest = InviteAcceptanceRequestDTO.builder()
                .token("test-token-123")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "john.doe@example.com",
                "encodedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_TENANT"))
        );

        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testInvitation = AccountInvitation.builder()
                .id(1L)
                .email("john.doe@example.com")
                .token("test-token-123")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .used(false)
                .propertyId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();

        testInvitationDTO = AccountInvitationDTO.builder()
                .email("john.doe@example.com")
                .token("test-token-123")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .used(false)
                .propertyId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("john.doe@example.com"))
                .thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("john.doe@example.com");
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void register_ShouldReturnToken_WhenRegistrationIsSuccessful() throws Exception {
        when(userService.emailExists("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.registerUserWithRoles(any(UserDTO.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(userService).emailExists("john.doe@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userService).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        when(userService.emailExists("john.doe@example.com")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use."));

        verify(userService).emailExists("john.doe@example.com");
        verify(userService, never()).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void registerInvitedUser_ShouldReturnToken_WhenInvitationIsValid() throws Exception {
        when(accountInvitationService.findByToken("test-token-123"))
                .thenReturn(Optional.of(testInvitation));
        when(accountInvitationConverter.toDto(testInvitation)).thenReturn(testInvitationDTO);
        when(userService.emailExists("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.registerUserWithRoles(any(UserDTO.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");
        doNothing().when(leaseService).createInvitedLease(testUser, testInvitationDTO);
        doNothing().when(accountInvitationService).markInvitationUsed(testInvitationDTO);

        mockMvc.perform(post("/auth/register/invited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(accountInvitationService).findByToken("test-token-123");
        verify(userService).registerUserWithRoles(any(UserDTO.class));
        verify(leaseService).createInvitedLease(testUser, testInvitationDTO);
        verify(accountInvitationService).markInvitationUsed(testInvitationDTO);
    }

    @Test
    void registerInvitedUser_ShouldReturnInternalServerError_WhenTokenIsInvalid() throws Exception {
        when(accountInvitationService.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        inviteRequest.setToken("invalid-token");

        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/auth/register/invited")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inviteRequest)));
        });

        verify(accountInvitationService).findByToken("invalid-token");
        verify(userService, never()).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void registerInvitedUser_ShouldReturnBadRequest_WhenInvitationIsExpired() throws Exception {
        testInvitation.setExpiresAt(LocalDateTime.now().minusDays(1));
        testInvitationDTO.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(accountInvitationService.findByToken("test-token-123"))
                .thenReturn(Optional.of(testInvitation));
        when(accountInvitationConverter.toDto(testInvitation)).thenReturn(testInvitationDTO);

        mockMvc.perform(post("/auth/register/invited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invitation is no longer valid."));

        verify(accountInvitationService).findByToken("test-token-123");
        verify(userService, never()).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void registerInvitedUser_ShouldReturnBadRequest_WhenInvitationIsAlreadyUsed() throws Exception {
        testInvitation.setUsed(true);
        testInvitationDTO.setUsed(true);

        when(accountInvitationService.findByToken("test-token-123"))
                .thenReturn(Optional.of(testInvitation));
        when(accountInvitationConverter.toDto(testInvitation)).thenReturn(testInvitationDTO);

        mockMvc.perform(post("/auth/register/invited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invitation is no longer valid."));

        verify(accountInvitationService).findByToken("test-token-123");
        verify(userService, never()).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void registerInvitedUser_ShouldReturnBadRequest_WhenEmailAlreadyRegistered() throws Exception {
        when(accountInvitationService.findByToken("test-token-123"))
                .thenReturn(Optional.of(testInvitation));
        when(accountInvitationConverter.toDto(testInvitation)).thenReturn(testInvitationDTO);
        when(userService.emailExists("john.doe@example.com")).thenReturn(true);

        mockMvc.perform(post("/auth/register/invited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already registered."));

        verify(userService).emailExists("john.doe@example.com");
        verify(userService, never()).registerUserWithRoles(any(UserDTO.class));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authRequest)));
        });

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void register_ShouldHandlePasswordEncodingProperly() throws Exception {
        String rawPassword = "mySecretPassword123";
        String encodedPassword = "$2a$10$encodedPasswordHash";

        registerRequest.setPassword(rawPassword);

        when(userService.emailExists(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userService.registerUserWithRoles(any(UserDTO.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(registerRequest.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(passwordEncoder).encode(rawPassword);
        verify(userService).registerUserWithRoles(argThat(userDTO ->
                userDTO.getPassword().equals(encodedPassword)));
    }
}