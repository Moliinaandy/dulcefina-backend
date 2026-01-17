package com.dulcefina.controller;

import com.dulcefina.entity.Stock;
import com.dulcefina.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockRepository stockRepo;

    public StockController(StockRepository stockRepo) {
        this.stockRepo = stockRepo;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> all() {
        return ResponseEntity.ok(stockRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return stockRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Stock stock) {
        Stock saved = stockRepo.save(stock);
        return ResponseEntity.created(URI.create("/api/stock/" + saved.getStockId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Stock stock) {
        Stock existing = stockRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stock not found"));
        existing.setQuantity(stock.getQuantity());
        existing.setMinThreshold(stock.getMinThreshold());
        existing.setLocation(stock.getLocation());
        return ResponseEntity.ok(stockRepo.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        stockRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
