package com.immobile.real_estate_backend.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInvitationDTO {
    private String token;
    private String email;
    private Long propertyId;
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
}
