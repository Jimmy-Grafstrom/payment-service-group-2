package se.iths.paymentservicegroup2.dto;

import java.math.BigDecimal;

/**
 * DTO dispatched over RabbitMQ when a payment has been successfully completed.
 *
 * @param paymentId             the internal payment entity ID
 * @param orderId               the ID of the paid order
 * @param amount                the settled transaction amount
 * @param currency              the transaction currency
 * @param status                the updated payment status (COMPLETED)
 * @param stripePaymentIntentId the Stripe PaymentIntent ID
 */
public record PaymentConfirmationDto(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        String status,
        String stripePaymentIntentId
) {
}
