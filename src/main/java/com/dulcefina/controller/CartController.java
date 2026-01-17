package com.dulcefina.controller;

import com.dulcefina.dto.AddToCartRequest;
import com.dulcefina.dto.UpdateCartItemRequest;
import com.dulcefina.entity.Cart;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserAccountRepository userRepo;

    public CartController(CartService cartService,
                          UserAccountRepository userRepo) {
        this.cartService = cartService;
        this.userRepo = userRepo;
    }

    private UserAccount fetchUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        try {
            UserAccount user = fetchUser(userId);
            Cart cart = cartService.getCartByUser(user);
            return ResponseEntity.ok(cart);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody AddToCartRequest req) { // req ya tiene el unitPrice
        try {
            UserAccount user = fetchUser(userId);

            Cart cart = cartService.addItemToCart(user, req);

            return ResponseEntity.ok(cart);
        } catch (NoSuchElementException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{userId}/item/{cartItemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long userId,
                                        @PathVariable Long cartItemId,
                                        @RequestBody UpdateCartItemRequest req) {
        try {
            UserAccount user = fetchUser(userId);
            Cart cart = cartService.updateItemQuantity(user, cartItemId, req.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/item/{cartItemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        try {
            UserAccount user = fetchUser(userId);
            Cart cart = cartService.removeItem(user, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clear(@PathVariable Long userId) {
        try {
            UserAccount user = fetchUser(userId);
            cartService.clearCart(user);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/subtotal")
    public ResponseEntity<?> subtotal(@PathVariable Long userId) {
        try {
            UserAccount user = fetchUser(userId);
            Cart cart = cartService.getCartByUser(user);
            double subtotal = cartService.calculateCartSubtotal(cart);
            return ResponseEntity.ok(subtotal);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
