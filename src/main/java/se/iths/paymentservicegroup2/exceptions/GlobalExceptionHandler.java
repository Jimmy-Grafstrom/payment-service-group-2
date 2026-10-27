package se.iths.paymentservicegroup2.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler providing centralized error responses across all controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link OrderAlreadyPaidException} and maps to HTTP 409 Conflict.
     *
     * @param e the thrown exception
     * @return a {@link ResponseEntity} containing the error message
     */
    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<String> handleOrderAlreadyPaidException(OrderAlreadyPaidException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**
     * Handles {@link PaymentProviderException} and maps to HTTP 502 Bad Gateway.
     *
     * @param e the thrown exception
     * @return a {@link ResponseEntity} containing a sanitized error message
     */
    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<String> handlePaymentProvider(PaymentProviderException e) {
        log.error("Stripe error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Payment provider is unavailable or rejected the request");

    }

    /**
     * Handles {@link InvalidWebhookSignatureException} and maps to HTTP 400 Bad Request.
     *
     * @param ex the thrown exception
     * @return a {@link ResponseEntity} containing the error message
     */
    @ExceptionHandler(InvalidWebhookSignatureException.class)
    public ResponseEntity<String> handleInvalidSignature(InvalidWebhookSignatureException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link WebhookProcessingException} and maps to HTTP 400 Bad Request.
     *
     * @param ex the thrown exception
     * @return a {@link ResponseEntity} containing the error message
     */
    @ExceptionHandler(WebhookProcessingException.class)
    public ResponseEntity<String> handleWebhookProcessing(WebhookProcessingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
