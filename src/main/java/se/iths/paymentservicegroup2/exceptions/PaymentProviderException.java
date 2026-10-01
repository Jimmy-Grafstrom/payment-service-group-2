package se.iths.paymentservicegroup2.exceptions;

import com.stripe.exception.StripeException;

public class PaymentProviderException extends RuntimeException {
    public PaymentProviderException(String message) {
        super(message);
    }

    public PaymentProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
