package com.fya.creditos.event;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditRegisteredEvent(
        Long creditId,
        String customerName,
        BigDecimal creditAmount,
        String salesAgent,
        Instant registeredAt
) {
}
