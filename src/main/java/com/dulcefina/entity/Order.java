package com.dulcefina.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference // Para evitar recursión infinita
    private List<OrderItem> items;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status; // Ej: "PENDIENTE", "EN PREPARACIÓN", "ENVIADO", "CANCELADO"

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // --- Campos Adicionales para formulario de checkout

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "payment_method")
    private String paymentMethod; // Ej: "Tarjeta de Crédito"

    @Column(name = "payment_status")
    private String paymentStatus; // Ej: "PAGADO", "PENDIENTE"

    @Column(name = "notification_read")
    private Boolean notificationRead = false;

}