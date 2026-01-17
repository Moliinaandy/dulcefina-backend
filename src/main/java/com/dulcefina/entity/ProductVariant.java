package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant {

    @Id
    @Column(name = "variant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variantId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", unique = true, length = 100)
    private String sku;

    @Column(name = "name")
    private String name;

    @Column(name = "price_modifier")
    private Double priceModifier;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "unit")
    private String unit;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
