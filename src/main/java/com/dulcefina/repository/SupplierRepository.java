package com.dulcefina.repository;

import com.dulcefina.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Para validación al crear/actualizar
    boolean existsByName(String name);
    boolean existsByEmail(String email);

    // Para validación de actualización
    boolean existsByNameAndSupplierIdNot(String name, Long supplierId);
    boolean existsByEmailAndSupplierIdNot(String email, Long supplierId);
}