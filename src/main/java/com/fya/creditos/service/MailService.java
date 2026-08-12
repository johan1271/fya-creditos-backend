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
        message.setSubject("Nuevo crédito registrado: " + event.customerName());
        message.setText("""
                Se ha registrado un nuevo crédito.

                Cliente: %s
                Monto: %s
                Asesor de ventas: %s
                Fecha de registro: %s
                """.formatted(event.customerName(), event.creditAmount(), event.salesAgent(), event.registeredAt()));

        mailSender.send(message);
    }
}
