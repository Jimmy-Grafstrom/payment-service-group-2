package se.iths.paymentservicegroup2.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class initializing the Stripe Java SDK with the configured secret API key.
 */
@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String apiKey;

    /**
     * Post-construct callback that initializes the global Stripe API key.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
