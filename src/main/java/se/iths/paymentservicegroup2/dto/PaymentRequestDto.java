package se.iths.paymentservicegroup2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull
        Long orderId
) {
}
