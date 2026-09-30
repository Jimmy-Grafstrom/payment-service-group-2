package se.iths.paymentservicegroup2.dto;

import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long id,
        Long orderId,
        String stripeSessionId,
        String stripePaymentIntentId,
        String status,
        LocalDateTime createdAt,
        String checkoutUrl
) {
}
