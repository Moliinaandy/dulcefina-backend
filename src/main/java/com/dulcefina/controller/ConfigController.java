package com.dulcefina.controller;

import com.dulcefina.entity.Config;
import com.dulcefina.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<List<Config>> all() {
        return ResponseEntity.ok(configService.findAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> get(@PathVariable String key) {
        return configService.findByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Config config) {
        Config saved = configService.create(config);
        return ResponseEntity.created(URI.create("/api/configs/" + saved.getConfigKey())).body(saved);
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> update(@PathVariable String key, @RequestBody Config config) {
        try {
            Config updated = configService.update(key, config);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable String key) {
        try {
            configService.delete(key);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
