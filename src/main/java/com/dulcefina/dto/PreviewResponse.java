package com.dulcefina.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreviewResponse {
    private Double estimatedPrice;
    private List<RequiredRawDto> requiredRawMaterials;
    private List<String> previewLayerUrls;
}
