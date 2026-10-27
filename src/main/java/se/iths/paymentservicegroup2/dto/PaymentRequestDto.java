package se.iths.paymentservicegroup2.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a checkout session for an order.
 *
 * @param orderId the unique ID of the order to be paid
 */
public record PaymentRequestDto(
        @NotNull
        Long orderId
) {
}
