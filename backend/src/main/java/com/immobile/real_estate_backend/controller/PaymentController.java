package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.LeaseDTO;
import com.immobile.real_estate_backend.model.dto.PaymentDTO;
import com.immobile.real_estate_backend.model.dto.StripeSessionRequestDTO;
import com.immobile.real_estate_backend.model.dto.UserDTO;
import com.immobile.real_estate_backend.model.entity.Lease;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.model.enums.LeaseStatus;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.LeaseService;
import com.immobile.real_estate_backend.service.PaymentService;
import com.immobile.real_estate_backend.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final LeaseService leaseService;
    @Value("${stripe.keys.secret}")
    private String apiKey;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {

        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{leaseId}")
    public ResponseEntity<PaymentDTO> createPayment(
            @PathVariable Long leaseId,
            @RequestBody PaymentDTO paymentRequest) {

        paymentRequest.setLeaseId(leaseId);
        PaymentDTO payment = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @PathVariable Long paymentId) {

        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/owner/me")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByAuthenticatedOwner(Authentication authentication) {
        String email = authentication.getName();

        UserDTO owner = userService.getUserByEmailDTO(email);
        List<PaymentDTO> payments = paymentService.getPaymentsByLandlordId(owner.getUserId());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/lease/{leaseId}/paid-this-month")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Boolean> isPaidThisMonth(@PathVariable Long leaseId) {
        boolean paid = paymentService.isCurrentMonthPaid(leaseId);
        return ResponseEntity.ok(paid);
    }

    @GetMapping("/lease/{leaseId}/paid-this-cycle")
    @PreAuthorize("hasRole('TENANT') or hasRole('LANDLORD')")
    public ResponseEntity<Boolean> isPaidThisCycle(@PathVariable Long leaseId) {
        boolean paid = paymentService.isRentPaidForCurrentBillingCycle(leaseId);
        return ResponseEntity.ok(paid);
    }

    @PostMapping("/stripe/create-session")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody StripeSessionRequestDTO req) throws StripeException {
        Stripe.apiKey = apiKey;

        LeaseDTO lease = leaseService.getLeaseDTO(req.getLeaseId());

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new RuntimeException("Cannot pay rent for an inactive lease.");
        }

        long amountInCents = lease.getMonthlyRent().multiply(BigDecimal.valueOf(100)).longValue();

        System.out.println(amountInCents);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment/success?session_id={CHECKOUT_SESSION_ID}") // update if needed
                .setCancelUrl("http://localhost:5173/payment/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Monthly Rent Payment")
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("leaseId", lease.getLeaseId().toString())
                .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(Map.of("id", session.getId()));
    }

    @PostMapping("/stripe/confirm")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PaymentDTO> confirmStripePayment(@RequestParam("session_id") String sessionId) {
        PaymentDTO payment = paymentService.confirmStripePayment(sessionId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/lease/{leaseId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<PaymentDTO>> getAllPaymentsForLease(
            @PathVariable Long leaseId) {

        List<PaymentDTO> payments = paymentService.getPaymentsByLeaseId(leaseId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/lease/{leaseId}/latest-payment")
    @PreAuthorize("hasRole('TENANT') or hasRole('LANDLORD')")
    public ResponseEntity<PaymentDTO> getLatestPaymentForLease(@PathVariable Long leaseId) {
        PaymentDTO latest = paymentService.getLatestPaymentForLease(leaseId);
        return ResponseEntity.ok(latest);
    }

}