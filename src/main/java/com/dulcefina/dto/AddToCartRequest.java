package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddToCartRequest {

    private Long productId;
    private Integer quantity = 1;
    private String customization; // El JSON de la personalización
    private Double unitPrice;
}