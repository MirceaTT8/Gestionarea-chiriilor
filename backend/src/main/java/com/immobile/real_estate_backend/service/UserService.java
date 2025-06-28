package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.UserConverter;
import com.immobile.real_estate_backend.model.dto.RentCollectionDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.entity.Role;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.PaymentRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final RoleService roleService;
    private final PaymentRepository paymentRepository;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = userConverter.toUser(userDTO);

        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            List<Role> roles = userDTO.getRoles().stream()
                    .map(roleService::getRoleByName)
                    .toList();
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return userConverter.toUserDTO(saved);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public UserDTO getUserByEmailDTO(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null ? userConverter.toUserDTO(user) : null;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUsersDTO() {
        return getAllUsers().stream()
                .map(userConverter::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserDTO(Long id) {
        User user = getUser(id);
        return user != null ? userConverter.toUserDTO(user) : null;
    }

    @Transactional
    public void deleteUserById(Long id){
        if (!userRepository.existsByUserId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteByUserId(id);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setIsActive(false);
        user.setDeactivatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setIsActive(true);
        user.setDeactivatedAt(null);
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public User registerUserWithRoles(UserDTO userDTO) {
        User user = userConverter.toUser(userDTO);

        List<Role> roles = userDTO.getRoles().stream()
                .map(roleService::getRoleByName)
                .toList();

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public UserDTO updateUserPhone(Long userId, String phone) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        existingUser.setPhone(phone);
        User savedUser = userRepository.save(existingUser);
        return userConverter.toUserDTO(savedUser);
    }


}
