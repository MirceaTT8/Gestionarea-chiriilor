package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.RentCollectionDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    public UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsersDTO(), HttpStatus.OK);
    }

    @PostMapping
    public void addUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam Long userId) {
        userService.deleteUserById(userId);
    }

    @PreAuthorize("hasRole('LANDLORD') or hasRole('TENANT') or hasRole('ADMIN')")
    @PutMapping("/{userId}/phone")
    public ResponseEntity<UserDTO> updateUserPhone(
            @PathVariable Long userId,
            @RequestBody Map<String, String> phoneData
    ) {
        String phone = phoneData.get("phone");
        UserDTO updatedUser = userService.updateUserPhone(userId, phone);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('TENANT')")
    public ResponseEntity<?> getMe(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of("email", user.getUsername()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userService.getUserDTO(userId), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmailDTO(email));
    }

    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok().build();
    }

}
