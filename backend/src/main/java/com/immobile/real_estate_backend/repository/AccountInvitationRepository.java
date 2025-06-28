package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.AccountInvitation;
import com.immobile.real_estate_backend.model.entity.Lease;
import org.apache.logging.log4j.core.Filter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountInvitationRepository extends JpaRepository<AccountInvitation, Long> {
    Optional<AccountInvitation> findByToken(String token);

}
