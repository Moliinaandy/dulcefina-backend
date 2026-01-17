package com.dulcefina.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CreateSessionRequest {

    private Long userId;
    private Double amount;
    private Map<String, Object> checkoutData;
}
