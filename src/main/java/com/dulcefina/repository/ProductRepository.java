package com.dulcefina.repository;

import com.dulcefina.entity.Product;
import com.dulcefina.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    List<Product> findByCategory(Category category);
    List<Product> findByIsActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stock IS NOT NULL AND p.minStock IS NOT NULL AND p.stock <= p.minStock")
    List<Product> findProductsWithLowStock();
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.supplier WHERE p.isActive = true AND p.stock IS NOT NULL AND p.minStock IS NOT NULL AND p.stock <= p.minStock AND (p.lowStockNotified = false OR p.lowStockNotified IS NULL)")
    List<Product> findProductsWithLowStockNotNotified();
}
