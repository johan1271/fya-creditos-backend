package com.fya.creditos.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditResponse(
        Long id,
        String customerName,
        String idNumber,
        BigDecimal creditAmount,
        BigDecimal interestRate,
        Integer termMonths,
        String salesAgent,
        Instant registeredAt
) {
}
