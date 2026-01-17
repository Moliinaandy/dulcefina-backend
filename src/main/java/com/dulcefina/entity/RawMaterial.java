package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_material")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RawMaterial {

    @Id
    @Column(name = "raw_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rawId;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "unit_measure")
    private String unitMeasure;

    @Column(name = "cost_unit")
    private Double costUnit;

    @Column(name = "stock_quantity")
    private Double stockQuantity;

    @Column(name = "min_stock")
    private Double minStock;

    @Column(name = "allergens")
    private String allergens;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
