package com.fya.creditos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditResponse(
        Long id,
        String customerName,
        String idNumber,
        BigDecimal creditAmount,
        BigDecimal interestRate,
        Integer termMonths,
        String salesAgent,
        LocalDateTime registeredAt
) {
}
