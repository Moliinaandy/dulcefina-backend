package com.dulcefina.service.impl;

import com.dulcefina.entity.Product;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.ProductRepository;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final UserAccountRepository userRepo;
    private final ProductRepository productRepo;

    public FavoriteServiceImpl(UserAccountRepository userRepo, ProductRepository productRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Product> getFavorites(UserAccount user) {
        UserAccount userWithFavorites = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));
        userWithFavorites.getFavoriteProducts().size();
        return userWithFavorites.getFavoriteProducts();
    }

    @Override
    @Transactional
    public Set<Product> addFavorite(UserAccount user, Long productId) {
        UserAccount managedUser = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));
        Product productToAdd = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productId));

        managedUser.getFavoriteProducts().add(productToAdd);

        userRepo.save(managedUser);
        return managedUser.getFavoriteProducts();
    }

    @Override
    @Transactional
    public Set<Product> removeFavorite(UserAccount user, Long productId) {
        UserAccount managedUser = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + user.getUserId()));
        Product productToRemove = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productId));

        boolean removed = managedUser.getFavoriteProducts().remove(productToRemove);
        if (!removed) {
            System.out.println("Product " + productId + " was not in favorites for user " + user.getUserId());
        }

        userRepo.save(managedUser);
        return managedUser.getFavoriteProducts();
    }
}