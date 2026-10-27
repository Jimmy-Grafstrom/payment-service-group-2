package se.iths.paymentservicegroup2.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

/**
 * Spring AMQP / RabbitMQ configuration class.
 * Sets up the payment confirmation queue and JSON message serialization.
 */
@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue.payment-success:payment-queue}")
    private String queueName;

    /**
     * Declares the durable payment queue.
     *
     * @return a durable {@link Queue} instance
     */
    @Bean
    public Queue paymentQueue() {
        return new Queue(queueName, true);
    }

    /**
     * Configures JSON serialization for outbound RabbitMQ messages.
     *
     * @return a {@link JacksonJsonMessageConverter} instance
     */
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Configures the {@link RabbitTemplate} with the JSON message converter.
     *
     * @param connectionFactory the AMQP connection factory
     * @return the configured {@link RabbitTemplate}
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
