package se.iths.paymentservicegroup2.dto;

import java.math.BigDecimal;

public record PaymentOrderDetailsDto(
        Long id,
        BigDecimal amount,
        String currency,
        String status
) {
}
