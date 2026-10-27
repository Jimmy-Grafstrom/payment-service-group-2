package se.iths.paymentservicegroup2.dto;

import java.time.LocalDateTime;

/**
 * Response DTO returned after creating or retrieving a checkout session.
 *
 * @param id                    the internal payment entity ID
 * @param orderId               the ID of the associated order
 * @param stripeSessionId       the Stripe Checkout session ID
 * @param stripePaymentIntentId the Stripe PaymentIntent ID (if created)
 * @param status                the current status of the payment
 * @param createdAt             timestamp of when the payment was initiated
 * @param checkoutUrl           the URL where the customer can complete payment on Stripe
 */
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
