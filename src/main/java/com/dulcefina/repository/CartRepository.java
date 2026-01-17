package com.dulcefina.repository;

import com.dulcefina.entity.Cart;
import com.dulcefina.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(UserAccount user);
}
