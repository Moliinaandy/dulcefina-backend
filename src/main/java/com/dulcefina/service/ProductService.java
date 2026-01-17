package com.dulcefina.service;

import com.dulcefina.dto.ProductDto;
import com.dulcefina.entity.Category;
import com.dulcefina.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductDto> findAll();
    Optional<ProductDto> findById(Long id);
    Optional<ProductDto> findBySlug(String slug);
    List<ProductDto> findByCategory(Category category);
    List<ProductDto> findActive();
    ProductDto create(Product product);
    ProductDto update(Long id, Product product);
    ProductDto toDto(Product product);
    void delete(Long id);
}