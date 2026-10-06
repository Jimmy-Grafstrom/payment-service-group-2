package se.iths.paymentservicegroup2.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.iths.paymentservicegroup2.dto.PaymentOrderDetailsDto;

@Component
public class OrderClient {
    private final RestClient restClient;

    public OrderClient(@Value("${order-service.base-url:http://localhost:9000}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public PaymentOrderDetailsDto getOrder(Long orderId, String bearerToken) {
        return restClient.get()
                .uri("/order/{id}", orderId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(PaymentOrderDetailsDto.class);
    }
}
