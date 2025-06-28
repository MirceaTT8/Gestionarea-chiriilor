package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.LeaseConverter;
import com.immobile.real_estate_backend.model.dto.AccountInvitationDTO;
import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.dto.LeaseRequestDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.model.enums.LeaseTerminationStatus;
import com.immobile.real_estate_backend.model.enums.PropertyStatus;
import com.immobile.real_estate_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final ObjectMapper objectMapper;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final LeaseConverter leaseConverter;
    private final EmailService emailService;

    public LeaseDTO getLeaseByTenantEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User tenant = userOpt.get();
        Optional<Lease> leaseOpt = leaseRepository.findFirstByTenantAndStatus(tenant, LeaseStatus.ACTIVE);

        return leaseOpt.map(leaseConverter::toLeaseDTO).orElse(null);
    }

    public List<LeaseDTO> findLeasesByOwnerEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User owner = userOpt.get();
        List<Lease> leases = leaseRepository.findByPropertyOwner(owner);

        return leases.stream()
                .map(leaseConverter::toLeaseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaseDTO createLease(LeaseDTO leaseDTO) {

        User tenant = userRepository.findById(leaseDTO.getTenantId()).orElseThrow();

        Lease lease = leaseConverter.toLease(leaseDTO);

        Lease savedLease = leaseRepository.save(lease);


        try {

            ActivityLog logEntry = ActivityLog.builder()
                    .user(savedLease.getProperty().getOwner())
                    .actionType("CREATE_LEASE")
                    .entityType("Lease")
                    .entityId((savedLease.getLeaseId()))
                    .details(objectMapper.writeValueAsString(leaseConverter.toLeaseDTO(savedLease)))
                    .build();

            activityLogRepository.save(logEntry);

        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Lease creation failed");

        }
        return leaseConverter.toLeaseDTO(savedLease);

    }

    @Transactional
    public void createInvitedLease(User tenant, AccountInvitationDTO invitation) {
        Property property = propertyService.getProperty(invitation.getPropertyId());
        propertyService.updatePropertyStatus(property.getPropertyId(), PropertyStatus.RENTED);

        Lease lease = Lease.builder()
                .tenant(tenant)
                .property(property)
                .startDate(invitation.getStartDate())
                .endDate(invitation.getEndDate())
                .monthlyRent(invitation.getMonthlyRent())
                .status(LeaseStatus.PENDING)
                .build();

        leaseRepository.save(lease);
    }


    public void sendLeaseInvitationOnly(LeaseRequestDTO request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        emailService.sendAccountAndLeaseInvitation(
                request.getTenantEmail(),
                property.getPropertyId(),
                property.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMonthlyRent()
        );

    }

    public List<Lease> getAllLeases() {
        return leaseRepository.findAll();
    }

    public List<LeaseDTO> getAllLeasesDTO() {
        return getAllLeases().stream()
                .map(leaseConverter::toLeaseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public LeaseDTO updateLeaseStatus(Long leaseId, LeaseStatus status) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        lease.setStatus(status);
        leaseRepository.save(lease);

        return leaseConverter.toLeaseDTO(lease);
    }

    public void requestTermination(Long leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new EntityNotFoundException("Lease not found"));
        lease.setLeaseTerminationStatus(LeaseTerminationStatus.PENDING);
        lease.setTerminationRequestedAt(LocalDate.now());
        leaseRepository.save(lease);
    }

    public LeaseDTO handleTerminationDecision(Long leaseId, LeaseTerminationStatus decision) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new EntityNotFoundException("Lease not found"));

        lease.setLeaseTerminationStatus(decision);
        lease.setTerminationRequestedAt(LocalDate.now());

        if (decision == LeaseTerminationStatus.APPROVED) {
            lease.setStatus(LeaseStatus.TERMINATED);
            propertyService.updatePropertyStatus(lease.getProperty().getPropertyId(), PropertyStatus.RENTED);
        }

        leaseRepository.save(lease);
        return leaseConverter.toLeaseDTO(lease);
    }

    public List<LeaseDTO> getPendingTerminations() {
        return leaseRepository.findAllByLeaseTerminationStatus(LeaseTerminationStatus.PENDING)
                .stream()
                .map(leaseConverter::toLeaseDTO)
                .toList();
    }

    public List<LeaseDTO> findPendingLeases() {
        return leaseRepository.findAllByStatus(LeaseStatus.PENDING)
                .stream()
                .map(leaseConverter::toLeaseDTO)
                .toList();
    }


    public LeaseDTO getLeaseDTO(Long leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));
        return leaseConverter.toLeaseDTO(lease);
    }

    @Transactional
    public void deleteLeaseById(Long leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        try {
            leaseRepository.delete(lease);
        } catch (Exception e) {
            log.error("Unexpected error during deletion of lease ID {}: {}", leaseId, e.getMessage());
            throw new RuntimeException("Lease deletion failed");
        }
    }

    public List<Lease> getActiveLeases(Long userId) {
        return leaseRepository.findActiveLeasesByLandlord(userId);
    }

    public List<LeaseDTO> getActiveLeasesDTO(Long userId) {
        return getActiveLeases(userId).stream()
                .map(leaseConverter::toLeaseDTO)
                .collect(Collectors.toList());
    }

    public List<LeaseDTO> getLeaseTrends() {
        List<Lease> leases = leaseRepository.findAllWithStartDates();
        return leases.stream()
                .map(leaseConverter::toLeaseDTO)
                .toList();
    }

    public boolean tenantHasAnyLease(String tenantEmail) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(tenantEmail);
            if (userOpt.isEmpty()) {
                log.warn("User not found with email: {}", tenantEmail);
                return false;
            }

            User tenant = userOpt.get();
            List<Lease> tenantLeases = leaseRepository.findAllByTenant(tenant);

            boolean hasLease = !tenantLeases.isEmpty();
            return hasLease;

        } catch (Exception e) {
            log.error("Error checking if tenant has lease: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<LeaseDTO> getAllLeasesByTenantEmail(String tenantEmail) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(tenantEmail);
            if (userOpt.isEmpty()) {
                throw new UsernameNotFoundException("User not found with email: " + tenantEmail);
            }

            User tenant = userOpt.get();
            List<Lease> tenantLeases = leaseRepository.findAllByTenant(tenant);

            return tenantLeases.stream()
                    .map(leaseConverter::toLeaseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all leases for tenant {}: {}", tenantEmail, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<LeaseDTO> getTerminatedLeasesByTenantEmail(String tenantEmail) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(tenantEmail);
            if (userOpt.isEmpty()) {
                throw new UsernameNotFoundException("User not found with email: " + tenantEmail);
            }

            User tenant = userOpt.get();
            List<Lease> terminatedLeases = leaseRepository.findAllByTenantAndStatus(tenant, LeaseStatus.TERMINATED);

            return terminatedLeases.stream()
                    .map(leaseConverter::toLeaseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching terminated leases for tenant {}: {}", tenantEmail, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
