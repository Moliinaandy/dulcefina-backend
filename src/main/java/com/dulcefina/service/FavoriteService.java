package com.dulcefina.service;

import com.dulcefina.entity.Product;
import com.dulcefina.entity.UserAccount;
import java.util.Set;

public interface FavoriteService {

    Set<Product> getFavorites(UserAccount user);

    Set<Product> addFavorite(UserAccount user, Long productId);

    Set<Product> removeFavorite(UserAccount user, Long productId);
}