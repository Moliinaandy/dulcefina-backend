package com.dulcefina.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckoutRequest {

    private String customerName;
    private String shippingAddress;
    private String customerPhone;
    private String paymentMethod; // "TARJETA" (Stripe), "YAPE", "EFECTIVO", etc.
}
