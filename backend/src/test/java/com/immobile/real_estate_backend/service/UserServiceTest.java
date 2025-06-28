package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.UserConverter;
import com.immobile.real_estate_backend.model.dto.RentCollectionDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.entity.Role;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.PaymentRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .userId(1L)
                .name("TENANT")
                .build();

        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .isActive(true)
                .roles(List.of(testRole))
                .build();

        testUserDTO = UserDTO.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .isActive(true)
                .roles(List.of("TENANT"))
                .build();
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        when(userConverter.toUser(testUserDTO)).thenReturn(testUser);
        when(roleService.getRoleByName("TENANT")).thenReturn(testRole);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userConverter.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.createUser(testUserDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        assertEquals(testUserDTO.getFirstName(), result.getFirstName());
        verify(userRepository).save(testUser);
        verify(roleService).getRoleByName("TENANT");
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void getUserByEmail_ShouldThrowException_WhenEmailNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void emailExists_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        boolean result = userService.emailExists("john.doe@example.com");

        assertTrue(result);
    }

    @Test
    void emailExists_ShouldReturnFalse_WhenEmailNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        boolean result = userService.emailExists("nonexistent@example.com");

        assertFalse(result);
    }

    @Test
    void deactivateUser_ShouldDeactivateUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user ->
                !user.getIsActive() && user.getDeactivatedAt() != null));
    }

    @Test
    void deactivateUser_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.deactivateUser(999L));
    }

    @Test
    void activateUser_ShouldActivateUser_WhenUserExists() {
        testUser.setIsActive(false);
        testUser.setDeactivatedAt(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.activateUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user ->
                user.getIsActive() && user.getDeactivatedAt() == null));
    }

    @Test
    void registerUserWithRoles_ShouldCreateUserWithRoles() {
        when(userConverter.toUser(testUserDTO)).thenReturn(testUser);
        when(roleService.getRoleByName("TENANT")).thenReturn(testRole);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.registerUserWithRoles(testUserDTO);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(roleService).getRoleByName("TENANT");
        verify(userRepository).save(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void getUserByEmailDTO_ShouldReturnUserDTO() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(userConverter.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.getUserByEmailDTO("john.doe@example.com");

        assertNotNull(result);
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userConverter).toUserDTO(testUser);
    }
}