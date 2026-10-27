package se.iths.paymentservicegroup2.model;

/**
 * Enumeration representing the different states of a payment transaction.
 */
public enum PaymentStatus {
    /** Payment session created in Stripe; awaiting customer action or webhook confirmation. */
    PENDING,
    /** Payment successfully authorized and captured. */
    COMPLETED,
    /** Payment attempt failed. */
    FAILED,
    /** Payment session expired or cancelled by the user. */
    CANCELLED,
    /** Initial transient state while the Stripe session is being created. */
    CREATING
}
