package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequiredRawDto {
    private Long rawId;
    private String name;
    private Double requiredQuantity;
    private String unitMeasure;
    private Double availableQuantity;
    private boolean ok;
}
