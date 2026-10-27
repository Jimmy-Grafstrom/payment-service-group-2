package se.iths.paymentservicegroup2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.paymentservicegroup2.dto.PaymentRequestDto;
import se.iths.paymentservicegroup2.dto.PaymentResponseDto;
import se.iths.paymentservicegroup2.service.PaymentService;

/**
 * Controller for handling payment-related requests.
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Creates a checkout session for a payment via Stripe.
     *
     * @param request the {@link PaymentRequestDto} containing the order ID to be paid
     * @param jwt     the {@link Jwt} token of the authenticated user
     * @return a {@link ResponseEntity} with HTTP 201 (Created) containing the {@link PaymentResponseDto} with the checkout
    ￹URL
     */
    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponseDto> createCheckoutSession(
            @Valid @RequestBody PaymentRequestDto request,
            @AuthenticationPrincipal Jwt jwt) {
        PaymentResponseDto response = paymentService.createCheckoutSession(
                request.orderId(),
                jwt.getSubject(),
                "Bearer " + jwt.getTokenValue()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
