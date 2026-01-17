package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_order")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {

    @Id
    @Column(name = "po_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poId;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "exceted_date")
    private LocalDate expectedDate;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @Column(name = "total")
    private Double total;
}
