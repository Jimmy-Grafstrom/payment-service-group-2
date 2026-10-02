package se.iths.paymentservicegroup2.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import se.iths.paymentservicegroup2.dto.PaymentConfirmationDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.payment-success:payment-queue}")
    private String queueName;

    public void sendPaymentConfirmation(PaymentConfirmationDto dto) {
        rabbitTemplate.convertAndSend(queueName, dto);
        log.info("Payment confirmation sent to queue: '{}' for orderId: {}", queueName, dto.orderId());
    }
}
