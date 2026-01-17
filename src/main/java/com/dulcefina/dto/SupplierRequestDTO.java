package com.dulcefina.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequestDTO {
    private String name;
    private String email;
    private String contactPerson;
    private String phone;
}