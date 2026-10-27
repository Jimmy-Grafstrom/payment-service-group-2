package se.iths.paymentservicegroup2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.iths.paymentservicegroup2.dto.PaymentOrderDetailsDto;

/**
 * REST client for communicating with the external Order Service.
 * Used to fetch and verify order status before initiating payment flows.
 */
@Component
public class OrderClient {
    private final RestClient restClient;

    /**
     * Constructs the OrderClient with a configured base URL.
     *
     * @param baseUrl the base URL of the order service
     */
    public OrderClient(@Value("${order-service.base-url:http://localhost:9000}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Fetches order details for a specific order by ID.
     *
     * @param orderId     the ID of the order to retrieve
     * @param bearerToken the Bearer authorization token forwarded for authentication
     * @return a {@link PaymentOrderDetailsDto} containing order and customer information
     */
    public PaymentOrderDetailsDto getOrder(Long orderId, String bearerToken) {
        return restClient.get()
                .uri("/order/{id}", orderId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(PaymentOrderDetailsDto.class);
    }
}
