package com.dulcefina.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    private Long supplierId;
    private String name;
    private String email;
    private String contactPerson;
    private String phone;
}