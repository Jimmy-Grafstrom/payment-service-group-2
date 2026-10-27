package se.iths.paymentservicegroup2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when parsing or handling a webhook event fails unexpectedly.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WebhookProcessingException extends RuntimeException {

    /**
     * Constructs a new WebhookProcessingException with the specified detail message.
     *
     * @param message the detail message
     */
    public WebhookProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new WebhookProcessingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public WebhookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
