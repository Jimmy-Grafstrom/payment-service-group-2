package se.iths.paymentservicegroup2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.paymentservicegroup2.service.WebhookService;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        webhookService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook received");
    }
}
