package com.dulcefina.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomizationRequest {
    private Integer quantity = 1;
    private Long sizeOptionId;
    private List<Long> optionValueIds;
    private Double basePrice = 0.0;
}
