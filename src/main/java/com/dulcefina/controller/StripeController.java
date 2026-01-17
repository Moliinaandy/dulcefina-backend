package com.dulcefina.controller;

import com.dulcefina.dto.CreateSessionRequest;
import com.dulcefina.dto.ConfirmSessionRequest;
import com.dulcefina.entity.Order;
import com.dulcefina.service.impl.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            String clientSecret = stripeService.createPaymentIntent(amount);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", clientSecret);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CreateSessionRequest req) {
        try {
            Map<String,String> resp = stripeService.createCheckoutSession(req);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creando sesión Stripe: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-session")
    public ResponseEntity<?> confirmSession(@RequestBody ConfirmSessionRequest req) {
        try {
            Order order = stripeService.confirmSessionAndCreateOrder(req);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error confirmando sesión: " + e.getMessage());
        }
    }
}
