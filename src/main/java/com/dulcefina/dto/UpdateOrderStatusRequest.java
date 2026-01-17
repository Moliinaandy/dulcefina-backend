package com.dulcefina.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO para que el Admin actualice el estado
@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    private String status; // Ej: "EN PREPARACIÓN", "ENVIADO", "CANCELADO"
}
