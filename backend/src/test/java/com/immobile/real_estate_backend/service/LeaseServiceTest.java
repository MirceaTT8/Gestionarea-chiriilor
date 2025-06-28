package com.immobile.real_estate_backend.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaseServiceTest {

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private LeaseConverter leaseConverter;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private LeaseService leaseService;

    private User testTenant;
    private User testLandlord;
    private Property testProperty;
    private Lease testLease;
    private LeaseDTO testLeaseDTO;
    private AccountInvitationDTO testInvitationDTO;
    private LeaseRequestDTO testLeaseRequestDTO;

    @BeforeEach
    void setUp() {
        testTenant = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testLandlord = User.builder()
                .userId(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        testProperty = Property.builder()
                .propertyId(1L)
                .owner(testLandlord)
                .name("Test Property")
                .address("123 Test Street")
                .build();

        testLease = Lease.builder()
                .leaseId(1L)
                .tenant(testTenant)
                .property(testProperty)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyRent(BigDecimal.valueOf(1200.00))
                .status(LeaseStatus.ACTIVE)
                .build();

        testLeaseDTO = LeaseDTO.builder()
                .leaseId(1L)
                .tenantId(1L)
                .propertyId(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyRent(BigDecimal.valueOf(1200.00))
                .status(LeaseStatus.ACTIVE)
                .build();

        testInvitationDTO = AccountInvitationDTO.builder()
                .email("john.doe@example.com")
                .propertyId(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .monthlyRent(BigDecimal.valueOf(1200.00))
                .build();

        testLeaseRequestDTO = new LeaseRequestDTO();
        testLeaseRequestDTO.setPropertyId(1L);
        testLeaseRequestDTO.setTenantEmail("john.doe@example.com");
        testLeaseRequestDTO.setStartDate(LocalDate.of(2024, 1, 1));
        testLeaseRequestDTO.setEndDate(LocalDate.of(2024, 12, 31));
        testLeaseRequestDTO.setMonthlyRent(BigDecimal.valueOf(1200.00));
    }

    @Test
    void getLeaseByTenantEmail_ShouldReturnLease_WhenTenantHasActiveLease() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testTenant));
        when(leaseRepository.findFirstByTenantAndStatus(testTenant, LeaseStatus.ACTIVE))
                .thenReturn(Optional.of(testLease));
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        LeaseDTO result = leaseService.getLeaseByTenantEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(testLeaseDTO.getLeaseId(), result.getLeaseId());
        assertEquals(testLeaseDTO.getTenantId(), result.getTenantId());
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(leaseRepository).findFirstByTenantAndStatus(testTenant, LeaseStatus.ACTIVE);
    }

    @Test
    void getLeaseByTenantEmail_ShouldReturnNull_WhenTenantHasNoActiveLease() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testTenant));
        when(leaseRepository.findFirstByTenantAndStatus(testTenant, LeaseStatus.ACTIVE))
                .thenReturn(Optional.empty());

        LeaseDTO result = leaseService.getLeaseByTenantEmail("john.doe@example.com");

        assertNull(result);
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(leaseRepository).findFirstByTenantAndStatus(testTenant, LeaseStatus.ACTIVE);
    }

    @Test
    void getLeaseByTenantEmail_ShouldThrowException_WhenTenantNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> leaseService.getLeaseByTenantEmail("nonexistent@example.com"));
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(leaseRepository, never()).findFirstByTenantAndStatus(any(User.class), any(LeaseStatus.class));
    }

    @Test
    void findLeasesByOwnerEmail_ShouldReturnLeases_WhenOwnerExists() {
        List<Lease> leases = List.of(testLease);
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.of(testLandlord));
        when(leaseRepository.findByPropertyOwner(testLandlord)).thenReturn(leases);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        List<LeaseDTO> result = leaseService.findLeasesByOwnerEmail("jane.smith@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLeaseDTO.getLeaseId(), result.get(0).getLeaseId());
        verify(userRepository).findByEmail("jane.smith@example.com");
        verify(leaseRepository).findByPropertyOwner(testLandlord);
    }

    @Test
    void createLease_ShouldCreateLeaseSuccessfully() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(leaseConverter.toLease(testLeaseDTO)).thenReturn(testLease);
        when(leaseRepository.save(testLease)).thenReturn(testLease);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());

        LeaseDTO result = leaseService.createLease(testLeaseDTO);

        assertNotNull(result);
        assertEquals(testLeaseDTO.getLeaseId(), result.getLeaseId());
        verify(userRepository).findById(1L);
        verify(leaseRepository).save(testLease);
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void createInvitedLease_ShouldCreateLeaseAndUpdatePropertyStatus() {
        when(propertyService.getProperty(1L)).thenReturn(testProperty);
        doNothing().when(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);
        when(leaseRepository.save(any(Lease.class))).thenReturn(testLease);

        leaseService.createInvitedLease(testTenant, testInvitationDTO);

        verify(propertyService).getProperty(1L);
        verify(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);
        verify(leaseRepository).save(any(Lease.class));
    }

    @Test
    void sendInvitationOnly_ShouldSendEmailInvitation() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        doNothing().when(emailService).sendAccountAndLeaseInvitation(
                anyString(), anyLong(), anyString(), any(LocalDate.class), any(LocalDate.class), any(BigDecimal.class));

        leaseService.sendLeaseInvitationOnly(testLeaseRequestDTO);

        verify(propertyRepository).findById(1L);
        verify(emailService).sendAccountAndLeaseInvitation(
                eq("john.doe@example.com"),
                eq(1L),
                eq("Test Property"),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31)),
                eq(BigDecimal.valueOf(1200.00))
        );
    }

    @Test
    void updateLeaseStatus_ShouldUpdateStatus_WhenLeaseExists() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseRepository.save(testLease)).thenReturn(testLease);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        LeaseDTO result = leaseService.updateLeaseStatus(1L, LeaseStatus.TERMINATED);

        assertNotNull(result);
        verify(leaseRepository).findById(1L);
        verify(leaseRepository).save(argThat(lease -> lease.getStatus() == LeaseStatus.TERMINATED));
        verify(leaseConverter).toLeaseDTO(testLease);
    }

    @Test
    void updateLeaseStatus_ShouldThrowException_WhenLeaseNotFound() {
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaseService.updateLeaseStatus(999L, LeaseStatus.TERMINATED));
        verify(leaseRepository).findById(999L);
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void requestTermination_ShouldSetTerminationStatus() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseRepository.save(testLease)).thenReturn(testLease);

        leaseService.requestTermination(1L);

        verify(leaseRepository).findById(1L);
        verify(leaseRepository).save(argThat(lease ->
                lease.getLeaseTerminationStatus() == LeaseTerminationStatus.PENDING &&
                        lease.getTerminationRequestedAt() != null));
    }

    @Test
    void handleTerminationDecision_ShouldApproveAndTerminate_WhenApproved() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseRepository.save(testLease)).thenReturn(testLease);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);
        doNothing().when(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);

        LeaseDTO result = leaseService.handleTerminationDecision(1L, LeaseTerminationStatus.APPROVED);

        assertNotNull(result);
        verify(leaseRepository).findById(1L);
        verify(leaseRepository).save(argThat(lease ->
                lease.getLeaseTerminationStatus() == LeaseTerminationStatus.APPROVED &&
                        lease.getStatus() == LeaseStatus.TERMINATED));
        verify(propertyService).updatePropertyStatus(1L, PropertyStatus.RENTED);
    }

    @Test
    void handleTerminationDecision_ShouldReject_WhenRejected() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseRepository.save(testLease)).thenReturn(testLease);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        LeaseDTO result = leaseService.handleTerminationDecision(1L, LeaseTerminationStatus.REJECTED);

        assertNotNull(result);
        verify(leaseRepository).findById(1L);
        verify(leaseRepository).save(argThat(lease ->
                lease.getLeaseTerminationStatus() == LeaseTerminationStatus.REJECTED &&
                        lease.getStatus() != LeaseStatus.TERMINATED));
        verify(propertyService, never()).updatePropertyStatus(anyLong(), any(PropertyStatus.class));
    }

    @Test
    void getPendingTerminations_ShouldReturnPendingTerminations() {
        List<Lease> pendingLeases = List.of(testLease);
        when(leaseRepository.findAllByLeaseTerminationStatus(LeaseTerminationStatus.PENDING))
                .thenReturn(pendingLeases);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        List<LeaseDTO> result = leaseService.getPendingTerminations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLeaseDTO.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findAllByLeaseTerminationStatus(LeaseTerminationStatus.PENDING);
    }

    @Test
    void findPendingLeases_ShouldReturnPendingLeases() {
        List<Lease> pendingLeases = List.of(testLease);
        when(leaseRepository.findAllByStatus(LeaseStatus.PENDING)).thenReturn(pendingLeases);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        List<LeaseDTO> result = leaseService.findPendingLeases();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLeaseDTO.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findAllByStatus(LeaseStatus.PENDING);
    }

    @Test
    void getLeaseDTO_ShouldReturnLeaseDTO_WhenLeaseExists() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        LeaseDTO result = leaseService.getLeaseDTO(1L);

        assertNotNull(result);
        assertEquals(testLeaseDTO.getLeaseId(), result.getLeaseId());
        verify(leaseRepository).findById(1L);
        verify(leaseConverter).toLeaseDTO(testLease);
    }

    @Test
    void getLeaseDTO_ShouldThrowException_WhenLeaseNotFound() {
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> leaseService.getLeaseDTO(999L));
        verify(leaseRepository).findById(999L);
        verify(leaseConverter, never()).toLeaseDTO(any(Lease.class));
    }

    @Test
    void deleteLeaseById_ShouldDeleteLease_WhenLeaseExists() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        doNothing().when(leaseRepository).delete(testLease);

        leaseService.deleteLeaseById(1L);

        verify(leaseRepository).findById(1L);
        verify(leaseRepository).delete(testLease);
    }

    @Test
    void deleteLeaseById_ShouldThrowException_WhenLeaseNotFound() {
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> leaseService.deleteLeaseById(999L));
        verify(leaseRepository).findById(999L);
        verify(leaseRepository, never()).delete(any(Lease.class));
    }

    @Test
    void getActiveLeases_ShouldReturnActiveLeases() {
        List<Lease> activeLeases = List.of(testLease);
        when(leaseRepository.findActiveLeasesByLandlord(2L)).thenReturn(activeLeases);

        List<Lease> result = leaseService.getActiveLeases(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLease.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findActiveLeasesByLandlord(2L);
    }

    @Test
    void getLeaseTrends_ShouldReturnLeasesWithStartDates() {
        List<Lease> leasesWithStartDates = List.of(testLease);
        when(leaseRepository.findAllWithStartDates()).thenReturn(leasesWithStartDates);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        List<LeaseDTO> result = leaseService.getLeaseTrends();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLeaseDTO.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findAllWithStartDates();
        verify(leaseConverter).toLeaseDTO(testLease);
    }

    @Test
    void getAllLeases_ShouldReturnAllLeases() {
        List<Lease> allLeases = List.of(testLease);
        when(leaseRepository.findAll()).thenReturn(allLeases);

        List<Lease> result = leaseService.getAllLeases();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLease.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findAll();
    }

    @Test
    void getAllLeasesDTO_ShouldReturnAllLeaseDTOs() {
        List<Lease> allLeases = List.of(testLease);
        when(leaseRepository.findAll()).thenReturn(allLeases);
        when(leaseConverter.toLeaseDTO(testLease)).thenReturn(testLeaseDTO);

        List<LeaseDTO> result = leaseService.getAllLeasesDTO();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLeaseDTO.getLeaseId(), result.get(0).getLeaseId());
        verify(leaseRepository).findAll();
        verify(leaseConverter).toLeaseDTO(testLease);
    }
}