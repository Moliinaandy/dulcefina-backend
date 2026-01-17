package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "production_item")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductionItem {

    @Id
    @Column(name = "production_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionItemId;

    @ManyToOne
    @JoinColumn(name = "production_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "time_est_minutes")
    private Integer timeEstMinutes;

    @Column(name = "time_real_minutes")
    private Integer timeRealMinutes;

    @Column(name = "qc_passed")
    private Boolean qcPassed;

    @Column(name = "observations")
    private String observations;
}
