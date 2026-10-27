package se.iths.paymentservicegroup2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Payment Service Spring Boot application.
 */
@SpringBootApplication
public class PaymentServiceGroup2Application {

    /**
     * Application bootstrapping method.
     *
     * @param args command-line arguments passed during startup
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceGroup2Application.class, args);
    }

}
