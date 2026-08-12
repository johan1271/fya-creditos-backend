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
        @NotBlank(message = "customerName must not be blank")
        @Size(max = 150, message = "customerName must be at most 150 characters")
        String customerName,

        @NotBlank(message = "idNumber must not be blank")
        @Size(min = 6, max = 20, message = "idNumber must be between 6 and 20 characters")
        String idNumber,

        @NotNull(message = "creditAmount must not be null")
        @Positive(message = "creditAmount must be greater than 0")
        BigDecimal creditAmount,

        @NotNull(message = "interestRate must not be null")
        @Positive(message = "interestRate must be greater than 0")
        @DecimalMax(value = "100", message = "interestRate must be at most 100")
        BigDecimal interestRate,

        @NotNull(message = "termMonths must not be null")
        @Min(value = 1, message = "termMonths must be at least 1")
        @Max(value = 360, message = "termMonths must be at most 360")
        Integer termMonths,

        @NotBlank(message = "salesAgent must not be blank")
        @Size(max = 150, message = "salesAgent must be at most 150 characters")
        String salesAgent
) {
}
