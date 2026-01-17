package com.dulcefina.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerificationRequest {
    private String email;
    private String code;

    private UserCreateRequest userData;
}