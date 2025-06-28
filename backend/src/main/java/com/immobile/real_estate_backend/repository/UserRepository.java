package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);
    User findUserByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'LANDLORD'")
    List<User> findAllLandlords();

    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("""
    SELECT u FROM User u
    JOIN u.roles r
    WHERE r.name = :role
""")
    List<User> findAllByRole(@Param("role") String roleName);

}
