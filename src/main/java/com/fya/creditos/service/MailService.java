package com.fya.creditos.service;

import com.fya.creditos.event.CreditRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailService {

    // registeredAt is stored as an Instant (UTC) precisely to stay unambiguous
    // regardless of where the app runs (see the entity/migration notes) — this
    // is the one place that instant should be rendered in a specific,
    // human-meaningful zone, since a person in Colombia reads this email.
    // 24h format, not am/pm — besides being the cultural norm in Colombia,
    // Java's Spanish am/pm strings ("p. m.") use a narrow no-break space
    // that some mail clients/consoles don't render cleanly.
    private static final DateTimeFormatter REGISTERED_AT_FORMATTER = DateTimeFormatter
            .ofPattern("d 'de' MMMM 'de' yyyy, HH:mm", new Locale("es", "CO"))
            .withZone(ZoneId.of("America/Bogota"));

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
                """.formatted(
                event.customerName(),
                event.creditAmount(),
                event.salesAgent(),
                REGISTERED_AT_FORMATTER.format(event.registeredAt())));

        mailSender.send(message);
    }
}
