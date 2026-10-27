package se.iths.paymentservicegroup2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;

import java.util.Optional;

/**
 * Spring Data JPA repository for persisting and querying {@link Payment} entities.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

    /**
     * Finds a payment record by its associated Stripe Checkout session ID.
     *
     * @param stripeSessionId the unique identifier of the Stripe session
     * @return an {@link Optional} containing the found {@link Payment}, or empty if none matches
     */
    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    /**
     * Checks if a payment exists for a specific order with the specified status.
     *
     * @param orderId the order ID
     * @param status  the {@link PaymentStatus}
     * @return {@code true} if a matching payment exists; {@code false} otherwise
     */
    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);

    /**
     * Finds a payment record by the internal order ID.
     *
     * @param orderId the ID of the order
     * @return an {@link Optional} containing the found {@link Payment}, or empty if none matches
     */
    Optional<Payment> findByOrderId(Long orderId);
}
