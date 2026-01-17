package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateCartItemRequest {
    private Integer quantity;
}
