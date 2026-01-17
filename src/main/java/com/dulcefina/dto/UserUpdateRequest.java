package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserUpdateRequest {
    private String email;
    private String fullName;
    private String phone;
    private Integer roleId;
    private Boolean isActive;
}
