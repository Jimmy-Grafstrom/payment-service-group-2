package se.iths.paymentservicegroup2.exceptions;

/**
 * Exception thrown when attempting to initiate payment for an order that is already completed.
 */
public class OrderAlreadyPaidException extends RuntimeException {

    /**
     * Constructs a new OrderAlreadyPaidException with the specified detail message.
     *
     * @param message the detail message
     */
    public OrderAlreadyPaidException(String message) {
        super(message);
    }
}
