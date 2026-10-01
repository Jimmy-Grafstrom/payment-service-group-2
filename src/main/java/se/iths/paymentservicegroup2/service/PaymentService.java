package se.iths.paymentservicegroup2.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.iths.paymentservicegroup2.dto.PaymentRequestDto;
import se.iths.paymentservicegroup2.dto.PaymentResponseDto;
import se.iths.paymentservicegroup2.exceptions.OrderAlreadyPaidException;
import se.iths.paymentservicegroup2.exceptions.PaymentProviderException;
import se.iths.paymentservicegroup2.model.Payment;
import se.iths.paymentservicegroup2.model.PaymentStatus;
import se.iths.paymentservicegroup2.repository.PaymentRepository;

import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public PaymentResponseDto createCheckoutSession(PaymentRequestDto paymentRequestDto, String userId) {

        if (repository.existsByOrderIdAndStatus(paymentRequestDto.orderId(), PaymentStatus.COMPLETED)) {
            throw new OrderAlreadyPaidException("Order " + paymentRequestDto.orderId() + " is already paid.");
        }

        long amountInOre = paymentRequestDto.amount()
                .setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(paymentRequestDto.orderId().toString())
                .putMetadata("orderId", paymentRequestDto.orderId().toString())
                .putMetadata("userId", userId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(paymentRequestDto.currency().toLowerCase())
                                .setUnitAmount(amountInOre)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order #" + paymentRequestDto.orderId())
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
        payment.setOrderId(paymentRequestDto.orderId());
        payment.setUserId(userId);
        payment.setStripeSessionId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setAmount(paymentRequestDto.amount());
        payment.setCurrency(paymentRequestDto.currency().toLowerCase());
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
