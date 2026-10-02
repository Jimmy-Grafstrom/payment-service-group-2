package se.iths.paymentservicegroup2.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.iths.paymentservicegroup2.dto.PaymentConfirmationDto;
import se.iths.paymentservicegroup2.exceptions.InvalidWebhookSignatureException;
import se.iths.paymentservicegroup2.exceptions.WebhookProcessingException;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;
import se.iths.paymentservicegroup2.publisher.PaymentPublisher;
import se.iths.paymentservicegroup2.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentPublisher paymentPublisher;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public void handleWebhook(String payload, String sigHeader) {
        Event event = parseAndVerifyEvent(payload, sigHeader);

        log.info("Received Stripe event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted(event);
            case "checkout.session.expired" -> handleCheckoutSessionExpired(event);
            default -> log.debug("Unhandled event type: {}", event.getType());
        }
    }

    private Event parseAndVerifyEvent(String payload, String sigHeader) {
        try {
            if (webhookSecret != null && !webhookSecret.isBlank()) {
                if (sigHeader != null && sigHeader.isBlank()) {
                    throw new InvalidWebhookSignatureException("Saknar Stripe-Signature header");
                }
                return Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                log.warn("Stripe-Signature header is missing or empty. Skipping signature verification.");
                return Webhook.constructEventWithoutVerification(payload);
            }
        } catch (SignatureVerificationException e) {
            log.error("Stripe signaturverifiering misslyckades: {}", e.getMessage());
            throw new InvalidWebhookSignatureException("Ogiltig Stripe-signatur", e);
        } catch (InvalidWebhookSignatureException e) {
            throw e;
        } catch (Exception e) {
            log.error("Fel vid parsning av Stripe event: {}", e.getMessage());
            throw new WebhookProcessingException("Kunde inte parsa webhook payload", e);
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (stripeObject instanceof Session session) {
            String sessionId = session.getId();
            log.info("Checkout session completed: {}", sessionId);

            Payment payment = paymentRepository.findByStripeSessionId(sessionId).orElse(null);
            if (payment == null) {
                log.warn("Payment not found for session ID: {}", sessionId);
                return;
            }

            payment.setStatus(PaymentStatus.COMPLETED);
            if (session.getPaymentIntent() != null) {
                payment.setStripePaymentIntentId(session.getPaymentIntent());
            }
            paymentRepository.save(payment);
            log.info("Payment status updated to COMPLETED for payment ID: {}", payment.getId());

            PaymentConfirmationDto confirmationDto = new PaymentConfirmationDto(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getStatus().name(),
                    payment.getStripePaymentIntentId()
            );
            paymentPublisher.sendPaymentConfirmation(confirmationDto);
        }
    }

    private void handleCheckoutSessionExpired(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (stripeObject instanceof Session session) {
            paymentRepository.findByStripeSessionId(session.getId()).ifPresent(payment -> {
                payment.setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
                log.info("Payment status updated to CANCELLED for payment ID: {}", payment.getId());
            });
        }
    }
}
