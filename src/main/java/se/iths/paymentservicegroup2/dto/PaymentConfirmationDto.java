package se.iths.paymentservicegroup2.dto;

import java.math.BigDecimal;

public record PaymentConfirmationDto(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        String status,
        String stripePaymentIntentId
) {
}
