package se.iths.paymentservicegroup2.exceptions;

import com.stripe.exception.StripeException;

/**
 * Exception thrown when an external payment provider (e.g. Stripe) returns an error or is unreachable.
 */
public class PaymentProviderException extends RuntimeException {

    /**
     * Constructs a new PaymentProviderException with the specified detail message.
     *
     * @param message the detail message
     */
    public PaymentProviderException(String message) {
        super(message);
    }

    /**
     * Constructs a new PaymentProviderException with the specified detail message and root cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public PaymentProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
