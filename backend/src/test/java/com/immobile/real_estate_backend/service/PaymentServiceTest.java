package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.PaymentConverter;
import com.immobile.real_estate_backend.model.dto.PaymentDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.PaymentMethod;
import com.immobile.real_estate_backend.model.enums.PaymentStatus;
import com.immobile.real_estate_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private PaymentConverter paymentConverter;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Lease testLease;
    private Payment testPayment;
    private PaymentDTO testPaymentDTO;
    private User testTenant;
    private User testLandlord;
    private Property testProperty;

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
                .monthlyRent(BigDecimal.valueOf(1200.00))
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        testPayment = Payment.builder()
                .paymentId(1L)
                .lease(testLease)
                .amount(BigDecimal.valueOf(1200.00))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .build();

        testPaymentDTO = PaymentDTO.builder()
                .paymentId(1L)
                .leaseId(1L)
                .amount(BigDecimal.valueOf(1200.00))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDate.now())
                .build();
    }

    @Test
    void processPayment_ShouldProcessPaymentSuccessfully() {
        // Core business logic mocks
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
//        when(paymentRepository.existsPaymentForMonth(eq(testLease), anyInt(), anyInt(), eq(PaymentStatus.COMPLETED)))
//                .thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        // ✅ Use lenient for optional operations
        lenient().when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());
        lenient().when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(new ActivityLog());

        PaymentDTO result = paymentService.processPayment(testPaymentDTO);

        assertNotNull(result);
        assertEquals(testPaymentDTO.getAmount(), result.getAmount());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_ShouldThrowException_WhenLeaseNotFound() {
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());
        testPaymentDTO.setLeaseId(999L);

        assertThrows(RuntimeException.class, () -> paymentService.processPayment(testPaymentDTO));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPayment_ShouldThrowException_WhenAmountMismatch() {
        testPaymentDTO.setAmount(BigDecimal.valueOf(1000.00)); // Different from lease rent

        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.processPayment(testPaymentDTO));
        assertEquals("Payment amount must exactly match the monthly rent", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getPaymentsByLeaseId_ShouldReturnPayments_WhenLeaseExists() {
        List<Payment> payments = List.of(testPayment);
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(paymentRepository.findByLeaseLeaseId(1L)).thenReturn(payments);
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        List<PaymentDTO> result = paymentService.getPaymentsByLeaseId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentDTO.getAmount(), result.get(0).getAmount());
        verify(leaseRepository).findById(1L);
        verify(paymentRepository).findByLeaseLeaseId(1L);
    }

    @Test
    void getPaymentsByLeaseId_ShouldThrowException_WhenLeaseNotFound() {
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.getPaymentsByLeaseId(999L));
        verify(paymentRepository, never()).findByLeaseLeaseId(anyLong());
    }

    @Test
    void getPaymentById_ShouldReturnPayment_WhenPaymentExists() {
        when(paymentRepository.findByPaymentId(1L)).thenReturn(Optional.of(testPayment));
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        PaymentDTO result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(testPaymentDTO.getAmount(), result.getAmount());
        verify(paymentRepository).findByPaymentId(1L);
    }

    @Test
    void getPaymentById_ShouldThrowException_WhenPaymentNotFound() {
        when(paymentRepository.findByPaymentId(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.getPaymentById(999L));
    }

    @Test
    void isCurrentMonthPaid_ShouldReturnTrue_WhenPaymentExistsThisMonth() {
        testPayment.setPaymentDate(LocalDate.now());
        List<Payment> payments = List.of(testPayment);
        when(paymentRepository.findByLeaseLeaseId(1L)).thenReturn(payments);

        boolean result = paymentService.isCurrentMonthPaid(1L);

        assertTrue(result);
        verify(paymentRepository).findByLeaseLeaseId(1L);
    }

    @Test
    void isCurrentMonthPaid_ShouldReturnFalse_WhenNoPaymentThisMonth() {
        testPayment.setPaymentDate(LocalDate.now().minusMonths(1));
        List<Payment> payments = List.of(testPayment);
        when(paymentRepository.findByLeaseLeaseId(1L)).thenReturn(payments);

        boolean result = paymentService.isCurrentMonthPaid(1L);

        assertFalse(result);
        verify(paymentRepository).findByLeaseLeaseId(1L);
    }

    @Test
    void isRentPaidForCurrentBillingCycle_ShouldReturnTrue_WhenPaidInCycle() {
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(paymentRepository.existsByLeaseAndPaymentDateBetween(eq(testLease), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(true);

        boolean result = paymentService.isRentPaidForCurrentBillingCycle(1L);

        assertTrue(result);
        verify(leaseRepository).findById(1L);
        verify(paymentRepository).existsByLeaseAndPaymentDateBetween(eq(testLease), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getLatestPaymentForLease_ShouldReturnLatestPayment() {
        when(paymentRepository.findTopByLeaseLeaseIdOrderByPaymentDateDesc(1L))
                .thenReturn(Optional.of(testPayment));
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        PaymentDTO result = paymentService.getLatestPaymentForLease(1L);

        assertNotNull(result);
        assertEquals(testPaymentDTO.getAmount(), result.getAmount());
        verify(paymentRepository).findTopByLeaseLeaseIdOrderByPaymentDateDesc(1L);
    }

    @Test
    void getLatestPaymentForLease_ShouldThrowException_WhenNoPaymentsFound() {
        when(paymentRepository.findTopByLeaseLeaseIdOrderByPaymentDateDesc(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.getLatestPaymentForLease(1L));
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() {
        List<Payment> payments = List.of(testPayment);
        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        List<PaymentDTO> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentDTO.getAmount(), result.get(0).getAmount());
        verify(paymentRepository).findAll();
    }

    @Test
    void getPaymentsByLandlordId_ShouldReturnLandlordPayments() {
        List<Payment> payments = List.of(testPayment);
        when(paymentRepository.findPaymentByLandlordId(2L)).thenReturn(payments);
        when(paymentConverter.toPaymentDTO(testPayment)).thenReturn(testPaymentDTO);

        List<PaymentDTO> result = paymentService.getPaymentsByLandlordId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentDTO.getAmount(), result.get(0).getAmount());
        verify(paymentRepository).findPaymentByLandlordId(2L);
    }
}