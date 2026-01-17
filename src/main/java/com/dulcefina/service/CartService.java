package com.dulcefina.service;

import com.dulcefina.dto.AddToCartRequest;
import com.dulcefina.entity.Cart;
import com.dulcefina.entity.UserAccount;

public interface CartService {
    Cart getCartByUser(UserAccount user);
    Cart createCartForUser(UserAccount user);

    Cart addItemToCart(UserAccount user, AddToCartRequest request);

    Cart updateItemQuantity(UserAccount user, Long cartItemId, int quantity);
    Cart removeItem(UserAccount user, Long cartItemId);
    void clearCart(UserAccount user);
    double calculateCartSubtotal(Cart cart);
}