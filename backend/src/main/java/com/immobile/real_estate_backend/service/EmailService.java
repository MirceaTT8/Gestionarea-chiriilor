package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.entity.Lease;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AccountInvitationService accountInvitationService;

    public void sendAccountAndLeaseInvitation(String email, Long propertyId, String propertyName, LocalDate startDate, LocalDate endDate, BigDecimal rent) {
        String token = accountInvitationService.generateToken(email, propertyId, startDate, endDate, rent);
        String invitationLink = "http://localhost:5173/invite?token=" + token;

        String message = """
    Hello,

    You've been invited to create an account for property leasing.

    ➤ Click here to create your account: %s

    Once your account is created, you'll be able to review and accept the pending lease.

    Property: %s
    Lease Period: %s to %s
    Monthly Rent: $%.2f

    Thank you,
    The Team
    """.formatted(invitationLink, propertyName, startDate, endDate, rent);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("You're Invited to Lease a Property");
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

}
