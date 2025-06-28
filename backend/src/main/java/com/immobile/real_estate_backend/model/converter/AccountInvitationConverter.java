package com.immobile.real_estate_backend.model.converter;

import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.model.entity.AccountInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountInvitationConverter {

    public AccountInvitationDTO toDto(AccountInvitation invitation) {
        return AccountInvitationDTO.builder()
                .token(invitation.getToken())
                .email(invitation.getEmail())
                .propertyId(invitation.getPropertyId())
                .expiresAt(invitation.getExpiresAt())
                .used(invitation.isUsed())
                .startDate(invitation.getStartDate())
                .endDate(invitation.getEndDate())
                .monthlyRent(invitation.getMonthlyRent())
                .build();
    }

    public AccountInvitation toEntity(AccountInvitationDTO dto) {
        return AccountInvitation.builder()
                .token(dto.getToken())
                .email(dto.getEmail())
                .propertyId(dto.getPropertyId())
                .expiresAt(dto.getExpiresAt())
                .used(dto.isUsed())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .monthlyRent(dto.getMonthlyRent())
                .build();
    }
}
