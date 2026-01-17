package com.dulcefina.dto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private String slug;
    private String shortDescription;
    private Double priceBase;
    private Integer stock;
    private Integer minStock;
    private List<String> imageUrls;
    private CategoryRefDTO category;
    private SupplierRefDTO supplier;
    private String description;
    private Integer timeProductionEstMin;
    private Boolean isActive;
    private String createdAt;
}