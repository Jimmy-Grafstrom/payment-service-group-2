package se.iths.paymentservicegroup2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.iths.paymentservicegroup2.dto.PaymentConfirmationDto;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;
import se.iths.paymentservicegroup2.publisher.PaymentPublisher;
import se.iths.paymentservicegroup2.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentPublisher paymentPublisher;

    @InjectMocks
    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(webhookService, "webhookSecret", "");
    }

    @Test
    void handleWebhook_whenCheckoutSessionCompleted_shouldUpdatePaymentAndPublishEvent() {
        // Arrange
        String sessionId = "cs_test_12345";
        String paymentIntentId = "pi_test_67890";

        String payload = """
                         {
                "id": "evt_test123",
                "object": "event",
                "api_version": "2026-08-26.dahlia",
                "type": "checkout.session.completed",
                "data": {
                             "object": {
                               "id": "%s",
                               "object": "checkout.session",
                               "payment_intent": "%s"
                             }
                }
                         }
                """.formatted(sessionId, paymentIntentId);

        Payment existingPayment = new Payment();
        existingPayment.setId(1L);
        existingPayment.setOrderId(42L);
        existingPayment.setAmount(BigDecimal.valueOf(499.00));
        existingPayment.setCurrency("SEK");
        existingPayment.setStatus(PaymentStatus.PENDING);
        existingPayment.setStripeSessionId(sessionId);

        when(paymentRepository.findByStripeSessionId(sessionId)).thenReturn(Optional.of(existingPayment));

        // Act
        webhookService.handleWebhook(payload, null);

        // Assert
        // 1. Verifiera att betalningen sparades med status COMPLETED
        assertThat(existingPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(existingPayment.getStripePaymentIntentId()).isEqualTo(paymentIntentId);
        verify(paymentRepository).save(existingPayment);

        // 2. Fånga och verifiera meddelandet som skickades till RabbitMQ
        ArgumentCaptor<PaymentConfirmationDto> captor = ArgumentCaptor.forClass(PaymentConfirmationDto.
                class);
        verify(paymentPublisher).sendPaymentConfirmation(captor.capture());

        PaymentConfirmationDto sentDto = captor.getValue();
        assertThat(sentDto.orderId()).isEqualTo(42L);
        assertThat(sentDto.paymentId()).isEqualTo(1L);
        assertThat(sentDto.status()).isEqualTo("COMPLETED");
        assertThat(sentDto.amount()).isEqualTo(BigDecimal.valueOf(499.00));
        assertThat(sentDto.stripePaymentIntentId()).isEqualTo(paymentIntentId);
    }
}