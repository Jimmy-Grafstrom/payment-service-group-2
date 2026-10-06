package se.iths.paymentservicegroup2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);

    Optional<Payment> findByOrderId(Long orderId);
}
