package se.iths.paymentservicegroup2.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import se.iths.paymentservicegroup2.dto.PaymentConfirmationDto;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentPublisher paymentPublisher;

    private final String queueName = "test-payment-queue";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentPublisher, "queueName", queueName);
    }

    @Test
    void sendPaymentConfirmation_shouldSendDtoToRabbitTemplate() {
        // Arrange
        PaymentConfirmationDto dto = new PaymentConfirmationDto(
                1L,
                100L,
                BigDecimal.valueOf(250.00),
                "SEK",
                "COMPLETED",
                "pi_test123"
        );

        // Act
        paymentPublisher.sendPaymentConfirmation(dto);

        // Assert
        verify(rabbitTemplate).convertAndSend(queueName, dto);
    }
}