package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.entity.Role;
import com.immobile.real_estate_backend.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String roleName) {
        Role role = roleRepository.findRoleByName(roleName);
        if (role == null) {
            throw new EntityNotFoundException("Role not found: " + roleName);
        }
        return role;
    }
}
