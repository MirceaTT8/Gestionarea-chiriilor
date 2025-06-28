package com.immobile.real_estate_backend.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long userId;
    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private String phone;
    private Long score;
    private Boolean isActive;
    private LocalDateTime deactivatedAt;
    private List<String> roles;

}
