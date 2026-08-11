package com.fya.creditos.service;

import com.fya.creditos.event.CreditRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.sender}")
    private String sender;

    @Value("${app.mail.notification-recipient}")
    private String notificationRecipient;

    public void sendCreditRegisteredNotification(CreditRegisteredEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(notificationRecipient);
        message.setSubject("New credit registered: " + event.customerName());
        message.setText("""
                A new credit has been registered.

                Customer: %s
                Amount: %s
                Sales agent: %s
                Registered at: %s
                """.formatted(event.customerName(), event.creditAmount(), event.salesAgent(), event.registeredAt()));

        mailSender.send(message);
    }
}
