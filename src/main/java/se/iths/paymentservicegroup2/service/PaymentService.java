package se.iths.paymentservicegroup2.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.iths.paymentservicegroup2.client.OrderClient;
import se.iths.paymentservicegroup2.dto.PaymentOrderDetailsDto;
import se.iths.paymentservicegroup2.dto.PaymentResponseDto;
import se.iths.paymentservicegroup2.exceptions.OrderAlreadyPaidException;
import se.iths.paymentservicegroup2.exceptions.PaymentProviderException;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;
import se.iths.paymentservicegroup2.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
            throw new IllegalStateException("Ordertjänsten returnerade en oväntad order");
        }

        if (!"PENDING".equals(order.status())) {
            throw new IllegalStateException("Ordern kan inte betalas");
        }

        Payment payment = repository.findByOrderId(order.id()).orElse(null);

        if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new OrderAlreadyPaidException("Order " + orderId + " är redan betald.");
        }

        if (payment != null && payment.getStatus() == PaymentStatus.PENDING && payment.getStripeSessionId() != null) {
            try {
                Session existingSession = Session.retrieve(payment.getStripeSessionId());

                return new PaymentResponseDto(
                        payment.getId(),
                        payment.getOrderId(),
                        payment.getStripeSessionId(),
                        payment.getStripePaymentIntentId(),
                        payment.getStatus().name(),
                        payment.getCreatedAt(),
                        existingSession.getUrl());
            } catch (StripeException e) {
                throw new PaymentProviderException("Kunde inte hämta befintlig Stripe-session", e);
            }
        }

        if (payment == null) {
            payment = new Payment();
            payment.setOrderId(order.id());
            payment.setUserId(userId);
            payment.setAmount(order.amount());
            payment.setCurrency(order.currency().toLowerCase());
            payment.setCreatedAt(LocalDateTime.now());
            payment.setStatus(PaymentStatus.CREATING);
            payment.setStripeIdempotencyKey(UUID.randomUUID().toString());

            payment = repository.saveAndFlush(payment);
        }

        if (payment.getStatus() != PaymentStatus.CREATING) {
            throw new IllegalStateException("Betalningsförsöket är inte i läget CREATING");
        }

        long amountInOre = order.amount()
                .movePointRight(2)
                .longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(order.id().toString())
                .putMetadata("orderId", order.id().toString())
                .putMetadata("paymentId", payment.getId().toString())
                .putMetadata("userId", userId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(order.currency().toLowerCase())
                                .setUnitAmount(amountInOre)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                        .builder()
                                        .setName("Order #" + order.id())
                                        .build())
                                .build())
                        .build())
                .build();

        try {
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(payment.getStripeIdempotencyKey())
                    .build();

            Session session = Session.create(params, options);

            payment.setStripeSessionId(session.getId());
            payment.setStatus(PaymentStatus.PENDING);
            payment = repository.save(payment);

            return new PaymentResponseDto(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getStripeSessionId(),
                    payment.getStripePaymentIntentId(),
                    payment.getStatus().name(),
                    payment.getCreatedAt(),
                    session.getUrl());
        } catch (StripeException e) {
            throw new PaymentProviderException("Kunde inte skapa Stripe-session", e);
        }
    }
}