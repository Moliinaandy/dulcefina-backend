package com.dulcefina.service.impl;

import com.dulcefina.dto.AddToCartRequest;
import com.dulcefina.entity.*;
import com.dulcefina.repository.*;
import com.dulcefina.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserAccountRepository userAccountRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Cart getCartByUser(UserAccount user) {
        return cartRepository.findByUser(user).orElseGet(() -> createCartForUser(user));
    }

    @Override
    public Cart createCartForUser(UserAccount user) {
        Cart cart = Cart.builder()
                .user(user)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    @Override
    public Cart addItemToCart(UserAccount user, AddToCartRequest request) {
        UserAccount persistedUser = userAccountRepository.findById(user.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));

        Cart cart = cartRepository.findByUser(persistedUser).orElseGet(() -> createCartForUser(persistedUser));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + request.getProductId()));

        double finalUnitPrice;
        if (request.getUnitPrice() != null) {
            finalUnitPrice = request.getUnitPrice();
        } else {
            finalUnitPrice = product.getPriceBase();
        }

        double subtotal = finalUnitPrice * request.getQuantity();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(finalUnitPrice)
                .subtotal(subtotal)
                .customization(request.getCustomization())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        cart.getItems().add(cartItem);
        cart.setUpdatedAt(java.time.LocalDateTime.now());
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart updateItemQuantity(UserAccount user, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("CartItem not found: " + cartItemId));

        item.setQuantity(quantity);
        item.setSubtotal(item.getUnitPrice() * quantity);
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        cart.setUpdatedAt(java.time.LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(UserAccount user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("CartItem not found: " + cartItemId));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(java.time.LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(UserAccount user) {
        UserAccount persistedUser = userAccountRepository.findById(user.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));
        Cart cart = cartRepository.findByUser(persistedUser).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cart.setUpdatedAt(java.time.LocalDateTime.now());
            cartRepository.save(cart);
        }
    }

    @Override
    public double calculateCartSubtotal(Cart cart) {
        if (cart == null || cart.getItems() == null) return 0.0;
        return cart.getItems().stream()
                .mapToDouble(ci -> ci.getSubtotal() != null ? ci.getSubtotal() : 0.0)
                .sum();
    }
}
