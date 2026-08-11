package com.fya.creditos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreditRequest(
        @NotBlank @Size(max = 150) String customerName,
        @NotBlank @Size(max = 20) String idNumber,
        @NotNull @Positive BigDecimal creditAmount,
        @NotNull @Positive @DecimalMax("100") BigDecimal interestRate,
        @NotNull @Min(1) @Max(360) Integer termMonths,
        @NotBlank @Size(max = 150) String salesAgent
) {
}
