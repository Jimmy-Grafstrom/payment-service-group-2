package se.iths.paymentservicegroup2.dto;

import java.math.BigDecimal;

/**
 * DTO representing order information retrieved from the Order Service.
 *
 * @param id       the order ID
 * @param amount   the total order amount
 * @param currency the three-letter ISO currency code (e.g., SEK, EUR, USD)
 * @param status   the current order status (e.g., PENDING)
 */
public record PaymentOrderDetailsDto(
        Long id,
        BigDecimal amount,
        String currency,
        String status
) {
}
