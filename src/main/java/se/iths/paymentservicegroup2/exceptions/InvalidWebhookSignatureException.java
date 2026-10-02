package se.iths.paymentservicegroup2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWebhookSignatureException extends RuntimeException {
    public InvalidWebhookSignatureException(String message) {
        super(message);
    }

    public InvalidWebhookSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
