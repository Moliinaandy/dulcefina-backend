package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "production_order")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductionOrder {

    @Id
    @Column(name = "production_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "production_code", unique = true, length = 60)
    private String productionCode;

    @Enumerated(EnumType.STRING)
    private ProductionStatus status = ProductionStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private UserAccount assignedTo;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "productionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionItem> items;
}
