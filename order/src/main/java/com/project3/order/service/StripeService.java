package com.project3.order.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public String createOrRetrieveCustomer(String email, String buyerId) throws StripeException {
        // Search for existing customer
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("query", "email:'" + email + "'");
        List<Customer> customers = Customer.search(searchParams).getData();

        if (customers.isEmpty()) {
            // Create new customer
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .putMetadata("buyerId", buyerId)
                    .build();
            Customer customer = Customer.create(params);
            return customer.getId();
        } else {
            return customers.get(0).getId();
        }
    }

    public PaymentIntent createPaymentIntent(double price, String customerId) throws StripeException {
        double serviceFee = price < 50 ? (5.5 / 100) * price + 2 : (5.5 / 100) * price;
        long amount = Math.round((price + serviceFee) * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .setCustomer(customerId)
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .build();

        return PaymentIntent.create(params);
    }
}
