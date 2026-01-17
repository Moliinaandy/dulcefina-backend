package com.dulcefina.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class PaymentRequestDTO {
    private String token;

    @JsonProperty("issuer_id")
    private String issuerId;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    private Integer installments;
    private String description;
    private PayerDTO payer;

    @Getter @Setter
    public static class PayerDTO {
        private String email;
        private IdentificationDTO identification;
    }

    @Getter @Setter
    public static class IdentificationDTO {
        private String type;
        private String number;
    }
}