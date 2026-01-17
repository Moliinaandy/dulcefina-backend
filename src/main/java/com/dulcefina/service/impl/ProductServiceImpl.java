package com.dulcefina.service.impl;

import com.dulcefina.dto.ProductDto;
import com.dulcefina.dto.CategoryRefDTO;
import com.dulcefina.dto.SupplierRefDTO;
import com.dulcefina.entity.ProductImage;
import com.dulcefina.entity.Category;
import com.dulcefina.entity.Product;
import com.dulcefina.entity.Supplier;
import com.dulcefina.repository.CategoryRepository;
import com.dulcefina.repository.ProductRepository;
import com.dulcefina.repository.SupplierRepository;
import com.dulcefina.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return productRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findByCategory(Category category) {
        return productRepository.findByCategory(category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findActive() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public ProductDto create(Product product) {
        if (product.getCategory() == null || product.getCategory().getCategoryId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada."));
        product.setCategory(category);

        if (product.getSupplier() != null && product.getSupplier().getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(product.getSupplier().getSupplierId())
                    .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado."));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    @Override
    public ProductDto update(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));

        existing.setName(product.getName());
        existing.setShortDescription(product.getShortDescription());
        existing.setDescription(product.getDescription());
        existing.setPriceBase(product.getPriceBase());
        existing.setTimeProductionEstMin(product.getTimeProductionEstMin());
        existing.setStock(product.getStock());
        existing.setMinStock(product.getMinStock());
        if (existing.getStock() != null && existing.getMinStock() != null
                && existing.getStock() > existing.getMinStock()) {
            existing.setLowStockNotified(false);
        }
        existing.setIsActive(product.getIsActive());
        existing.setSlug(product.getSlug());

        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada."));
            existing.setCategory(category);
        }

        if (product.getSupplier() != null && product.getSupplier().getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(product.getSupplier().getSupplierId())
                    .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado."));
            existing.setSupplier(supplier);
        } else {
            existing.setSupplier(null);
        }

        Product saved = productRepository.save(existing);
        return toDto(saved);
    }
    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .priceBase(product.getPriceBase())
                .stock(product.getStock())
                .minStock(product.getMinStock())
                .timeProductionEstMin(product.getTimeProductionEstMin())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .imageUrls(product.getImages() != null ?
                        product.getImages().stream()
                                .filter(ProductImage::getIsActive)
                                .sorted(Comparator.comparing(ProductImage::getPriority))
                                .map(ProductImage::getUrl)
                                .toList()
                        : List.of()
                )
                .category(product.getCategory() != null ?
                        new CategoryRefDTO(product.getCategory().getCategoryId(), product.getCategory().getName()) : null)
                .supplier(product.getSupplier() != null ?
                        new SupplierRefDTO(product.getSupplier().getSupplierId(), product.getSupplier().getName()) : null)
                .build();
    }
}
