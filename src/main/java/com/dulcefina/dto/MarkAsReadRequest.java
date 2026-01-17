package com.dulcefina.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MarkAsReadRequest {
    private List<String> ids; // Lista de IDs (ej: ["order-102", "product-5"])
}