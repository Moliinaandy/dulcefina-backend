package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase_item")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseItem {

    @Id
    @Column(name = "po_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poItemId;

    @ManyToOne
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "raw_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "subtotal")
    private Double subtotal;
}
