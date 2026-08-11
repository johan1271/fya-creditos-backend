package com.fya.creditos.event;

import com.fya.creditos.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditRegisteredListener {

    private final MailService mailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreditRegistered(CreditRegisteredEvent event) {
        try {
            mailService.sendCreditRegisteredNotification(event);
            log.info("Registration email sent for credit {}", event.creditId());
        } catch (Exception ex) {
            log.error("Failed to send registration email for credit {}", event.creditId(), ex);
        }
    }
}
