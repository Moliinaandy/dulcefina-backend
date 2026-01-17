package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionValueDto {
    private Long optionValueId;
    private String name;
    private String slug;
    private Double priceModifier;
    private Double multiplier;
    private String imageUrl;
}
