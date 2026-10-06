    package se.iths.paymentservicegroup2.service;

    import com.stripe.exception.StripeException;
    import com.stripe.model.checkout.Session;
    import com.stripe.param.checkout.SessionCreateParams;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import se.iths.paymentservicegroup2.client.OrderClient;
    import se.iths.paymentservicegroup2.dto.PaymentOrderDetailsDto;
    import se.iths.paymentservicegroup2.dto.PaymentResponseDto;
    import se.iths.paymentservicegroup2.exceptions.PaymentProviderException;
    import se.iths.paymentservicegroup2.model.Payment;
    import se.iths.paymentservicegroup2.model.PaymentStatus;
    import se.iths.paymentservicegroup2.repository.PaymentRepository;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.time.LocalDateTime;
    import java.util.Objects;

    @Service
    @RequiredArgsConstructor
    public class PaymentService {

        private final PaymentRepository repository;
        private final OrderClient orderClient;

        @Value("${stripe.success-url}")
        private String successUrl;

        @Value("${stripe.cancel-url}")
        private String cancelUrl;

        public PaymentResponseDto createCheckoutSession(Long orderId, String userId, String bearerToken) {

            PaymentOrderDetailsDto order = orderClient.getOrder(orderId, bearerToken);

            if (order == null || !Objects.equals(orderId, order.id())) {
                throw new IllegalStateException("Order service returned an unexpected order");
            }

            if (!"PENDING".equals(order.status())){
                throw new IllegalStateException("Order is not payable");
            }

            long amountInOre = order.amount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(order.id().toString())
                    .putMetadata("orderId", order.id().toString())
                    .putMetadata("userId", userId)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(order.currency().toLowerCase())
                                    .setUnitAmount(amountInOre)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Order #" + order.id())
                                            .build())
                                    .build())
                            .build())
                    .build();

            Session session;
            try {
                session = Session.create(params);
            } catch (StripeException e) {
                throw new PaymentProviderException("Could not create checkout session", e);
            }

            Payment payment = new Payment();
            payment.setOrderId(order.id());
            payment.setUserId(userId);
            payment.setStripeSessionId(session.getId());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setAmount(order.amount());
            payment.setCurrency(order.currency().toLowerCase());
            payment = repository.save(payment);

            return new PaymentResponseDto(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getStripeSessionId(),
                    null,
                    payment.getStatus().name(),
                    payment.getCreatedAt(),
                    session.getUrl());
        }
    }
