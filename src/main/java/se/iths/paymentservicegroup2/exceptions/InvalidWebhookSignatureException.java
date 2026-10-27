package se.iths.paymentservicegroup2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when cryptographic verification of an incoming Stripe webhook signature fails.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWebhookSignatureException extends RuntimeException {

    /**
     * Constructs a new InvalidWebhookSignatureException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidWebhookSignatureException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidWebhookSignatureException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public InvalidWebhookSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
