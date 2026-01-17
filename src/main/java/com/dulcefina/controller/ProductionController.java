package com.dulcefina.controller;

import com.dulcefina.entity.ProductionOrder;
import com.dulcefina.repository.ProductionOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/production")
public class ProductionController {

    private final ProductionOrderRepository productionRepo;

    public ProductionController(ProductionOrderRepository productionRepo) {
        this.productionRepo = productionRepo;
    }

    @GetMapping
    public ResponseEntity<List<ProductionOrder>> all() {
        return ResponseEntity.ok(productionRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return productionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductionOrder>> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(productionRepo.findByStatus(status));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @PathVariable String status) {
        ProductionOrder po = productionRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Production order not found"));
        po.setStatus(Enum.valueOf(com.dulcefina.entity.ProductionStatus.class, status.toUpperCase()));
        return ResponseEntity.ok(productionRepo.save(po));
    }
}
