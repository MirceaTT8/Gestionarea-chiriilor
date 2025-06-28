package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.config.JwtUtil;
import com.immobile.real_estate_backend.model.converter.AccountInvitationConverter;
import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.model.dto.InviteAcceptanceRequestDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.dto.auth.AuthRequest;
import com.immobile.real_estate_backend.model.dto.auth.AuthResponse;
import com.immobile.real_estate_backend.model.dto.auth.RegisterRequest;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.service.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authManager;

    private UserDetailsServiceImpl userDetailsService;

    private JwtUtil jwtUtil;

    private UserService userService;

    private LeaseService leaseService;

    private AccountInvitationService accountInvitationService;

    private PasswordEncoder passwordEncoder;

    private AccountInvitationConverter accountInvitationConverter;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.emailExists(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        UserDTO userDTO = UserDTO.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(List.of("LANDLORD"))
                .build();

        userService.registerUserWithRoles(userDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }


    @PostMapping("/register/invited")
    public ResponseEntity<?> registerInvitedUser(@RequestBody InviteAcceptanceRequestDTO request) {
        AccountInvitation invitation = accountInvitationService.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired invitation"));

        AccountInvitationDTO invitationDTO = accountInvitationConverter.toDto(invitation);

        if (invitationDTO.isUsed() || invitationDTO.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invitation is no longer valid.");
        }
        if (userService.emailExists(invitationDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered.");
        }

        UserDTO userDTO = UserDTO.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(invitationDTO.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(List.of("TENANT"))
                .build();

        User user = userService.registerUserWithRoles(userDTO);

        leaseService.createInvitedLease(user, invitationDTO);

        accountInvitationService.markInvitationUsed(invitationDTO);

        String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(userDTO.getEmail()));
        return ResponseEntity.ok(new AuthResponse(token));
    }


}

