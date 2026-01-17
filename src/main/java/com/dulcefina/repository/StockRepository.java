package com.dulcefina.repository;

import com.dulcefina.entity.Stock;
import com.dulcefina.entity.RawMaterial;
import com.dulcefina.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByRawMaterial(RawMaterial rawMaterial);
    Optional<Stock> findByVariant(ProductVariant variant);
    List<Stock> findByLocation(String location);
}
