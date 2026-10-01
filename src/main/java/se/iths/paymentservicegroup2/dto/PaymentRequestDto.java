package se.iths.paymentservicegroup2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull
        Long orderId,

        @NotNull
        @DecimalMin(value = "3.00", message = "Amount must be at least 0.5")
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount,
        @Pattern(regexp = "^[A-Za-z]{3}$")
        String currency
) {
}
