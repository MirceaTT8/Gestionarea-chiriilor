package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.entity.Role;
import com.immobile.real_estate_backend.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserConverter {

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .deactivatedAt(user.getDeactivatedAt())
                .phone(user.getPhone())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .toList()
                )
                .build();
    }


    public User toUser(UserDTO userDTO) {
        return User.builder()
                .userId(userDTO.getUserId())
                .lastName(userDTO.getLastName())
                .firstName(userDTO.getFirstName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .isActive(userDTO.getIsActive())
                .deactivatedAt(userDTO.getDeactivatedAt())
                .phone(userDTO.getPhone())
                .build();
    }
}
