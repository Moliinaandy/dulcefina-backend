package com.dulcefina.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dulcefina.dto.ProductDto;
import com.dulcefina.entity.Category;
import com.dulcefina.entity.Product;
import com.dulcefina.entity.ProductImage;
import com.dulcefina.repository.CategoryRepository;
import com.dulcefina.repository.ProductImageRepository;
import com.dulcefina.repository.ProductRepository;
import com.dulcefina.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final Cloudinary cloudinary;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService,
                             CategoryRepository categoryRepository,
                             ProductRepository productRepository,
                             ProductImageRepository productImageRepository,
                             Cloudinary cloudinary) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> all() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDto>> active() {
        return ResponseEntity.ok(productService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<ProductDto> p = productService.findById(id);
        return p.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        Optional<ProductDto> p = productService.findBySlug(slug);
        return p.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{slug}")
    public ResponseEntity<?> getByCategory(@PathVariable String slug) {
        Optional<Category> cat = categoryRepository.findBySlug(slug);
        if (cat.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productService.findByCategory(cat.get()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product) {
        ProductDto created = productService.create(product);
        return ResponseEntity.created(URI.create("/products/" + created.getProductId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        try {
            ProductDto updated = productService.update(id, product);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImage> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "priority", defaultValue = "0") Integer priority,
            @RequestParam(value = "isActive", defaultValue = "1") Boolean isActive
    ) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productId));

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "products/" + productId));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .url(url)
                    .altText(altText != null ? altText : file.getOriginalFilename())
                    .priority(priority)
                    .isActive(isActive)
                    .build();

            ProductImage saved = productImageRepository.save(productImage);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImage>> getImages(@PathVariable Long productId) {
        List<ProductImage> images = productImageRepository.findByProduct_ProductId(productId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        Optional<ProductImage> img = productImageRepository.findById(imageId);
        if (img.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productImageRepository.delete(img.get());
        return ResponseEntity.noContent().build();
    }
}

