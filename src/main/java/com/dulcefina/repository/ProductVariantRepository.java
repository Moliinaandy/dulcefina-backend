package com.dulcefina.repository;

import com.dulcefina.entity.ProductVariant;
import com.dulcefina.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku(String sku);
    List<ProductVariant> findByProduct(Product product);
}
