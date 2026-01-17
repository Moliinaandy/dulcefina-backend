package com.dulcefina.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "supplier")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @Column(name = "name", nullable = false, length = 200)
    private String name; // Ej: "Proveedor de Harinas S.A."

    @Column(name = "email", nullable = false, length = 150)
    private String email; // Ej: "pedidos@harinassa.com"

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "phone")
    private String phone;

    // Relación: Un proveedor puede tener muchos productos
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Product> products;
}