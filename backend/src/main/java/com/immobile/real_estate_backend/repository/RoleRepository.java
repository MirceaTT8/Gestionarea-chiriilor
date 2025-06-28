package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
}
