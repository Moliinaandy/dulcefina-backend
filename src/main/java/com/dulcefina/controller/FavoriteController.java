package com.dulcefina.controller;

import com.dulcefina.dto.AddFavoriteRequest;
import com.dulcefina.entity.Product;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserAccountRepository userRepo;

    public FavoriteController(FavoriteService favoriteService, UserAccountRepository userRepo) {
        this.favoriteService = favoriteService;
        this.userRepo = userRepo;
    }

    private UserAccount fetchUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavorites(@PathVariable Long userId) {
        try {
            UserAccount user = fetchUser(userId);
            Set<Product> favorites = favoriteService.getFavorites(user);
            return ResponseEntity.ok(favorites);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addFavorite(@PathVariable Long userId, @RequestBody AddFavoriteRequest request) {
        if (request.getProductId() == null) {
            return ResponseEntity.badRequest().body("Product ID is required.");
        }
        try {
            UserAccount user = fetchUser(userId);
            Set<Product> updatedFavorites = favoriteService.addFavorite(user, request.getProductId());
            return ResponseEntity.ok(updatedFavorites);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            UserAccount user = fetchUser(userId);
            Set<Product> updatedFavorites = favoriteService.removeFavorite(user, productId);
            return ResponseEntity.ok(updatedFavorites);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}