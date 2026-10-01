package se.iths.paymentservicegroup2.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<String> handleOrderAlreadyPaidException(OrderAlreadyPaidException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<String> handlePaymentProvider(PaymentProviderException e) {
        log.error("Stripe error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Payment provider is unavailable or rejected the request");
    }
}
