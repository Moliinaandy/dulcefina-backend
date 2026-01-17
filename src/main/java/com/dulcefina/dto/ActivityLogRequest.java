package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLogRequest {
    private Long userId;
    private String action;
    private String details;
}
