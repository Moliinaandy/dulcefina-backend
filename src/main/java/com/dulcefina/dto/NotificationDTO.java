package com.dulcefina.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDTO {
    private String id; // ID único (ej: "order-102" o "product-5")
    private String type; // "NEW_ORDER" o "LOW_STOCK"
    private String message;
    private LocalDateTime timestamp;
}