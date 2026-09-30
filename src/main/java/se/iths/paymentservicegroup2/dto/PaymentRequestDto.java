package se.iths.paymentservicegroup2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull
        Long orderId,

        @NotNull
        @DecimalMin(value = "0.5", message = "Amount must be at least 0.5")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        String currency
) {
}
