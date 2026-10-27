package se.iths.paymentservicegroup2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.paymentservicegroup2.service.WebhookService;

/**
 * Controller for handling Stripe webhook events.
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    /**
     * Receives and processes webhook events sent by Stripe.
     *
     * @param payload   the raw JSON payload from Stripe
     * @param sigHeader the Stripe-Signature header used for cryptographic verification
     * @return a {@link ResponseEntity} with HTTP 200 (OK) acknowledging receipt of the event
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        webhookService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received");
    }
}
