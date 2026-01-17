package com.dulcefina.repository;

import com.dulcefina.entity.Cart;
import com.dulcefina.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // KPI 4: Nuevos Clientes
    @Query("SELECT COUNT(u.userId) FROM UserAccount u WHERE u.createdAt >= :startDate")
    Long countNewUsers(@Param("startDate") LocalDateTime startDate);
}
