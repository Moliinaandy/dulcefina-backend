package com.dulcefina.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Integer roleId;
}
