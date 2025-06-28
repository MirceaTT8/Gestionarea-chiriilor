package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.AccountInvitationConverter;
import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.model.entity.AccountInvitation;
import com.immobile.real_estate_backend.repository.AccountInvitationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountInvitationService {

    private final AccountInvitationRepository accountInvitationRepository;
    private final AccountInvitationConverter accountInvitationConverter;

    public String generateToken(String email, Long propertyId, LocalDate startDate, LocalDate endDate, BigDecimal rent) {
        String token = UUID.randomUUID().toString();

        AccountInvitation invitation = AccountInvitation.builder()
                .email(email)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .used(false)
                .propertyId(propertyId)
                .startDate(startDate)
                .endDate(endDate)
                .monthlyRent(rent)
                .build();

        accountInvitationRepository.save(invitation);

        return token;
    }

    public List<AccountInvitationDTO> findAllInvitations() {
        return accountInvitationRepository.findAll().stream()
                .map(accountInvitationConverter::toDto)
                .toList();
    }
    public Optional<AccountInvitation> findByToken(String token) {
        return accountInvitationRepository.findByToken(token);
    }

    public void markInvitationUsed(AccountInvitationDTO invitationDTO) {
        invitationDTO.setUsed(true);
        accountInvitationRepository.save(accountInvitationConverter.toEntity(invitationDTO));
    }
}
