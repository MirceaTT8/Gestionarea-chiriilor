package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.service.AccountInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
public class AccountInvitationController {

    private final AccountInvitationService accountInvitationService;

    @GetMapping
    public ResponseEntity<List<AccountInvitationDTO>> getAllInvitations() {
        List<AccountInvitationDTO> invitations = accountInvitationService.findAllInvitations();
        return ResponseEntity.ok(invitations);
    }

}
