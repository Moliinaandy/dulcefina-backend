package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Integer roleId;
    private String roleName;
    private Boolean isActive;
    private String createdAt;
    private String lastLogin;
}
