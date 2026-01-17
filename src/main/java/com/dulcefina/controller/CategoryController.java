package com.dulcefina.controller;

import com.dulcefina.entity.Category;
import com.dulcefina.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepo;

    public CategoryController(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @GetMapping
    public ResponseEntity<List<Category>> all() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return categoryRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category) {
        Category saved = categoryRepo.save(category);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getCategoryId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Category cat) {
        Category existing = categoryRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        existing.setName(cat.getName());
        existing.setSlug(cat.getSlug());
        return ResponseEntity.ok(categoryRepo.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
