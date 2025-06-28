package com.immobile.real_estate_backend.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteAcceptanceRequestDTO {
    private String token;
    private String firstName;
    private String lastName;
    private String password;
}

