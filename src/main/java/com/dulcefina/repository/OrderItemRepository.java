package com.dulcefina.repository;

import com.dulcefina.entity.OrderItem;
import com.dulcefina.repository.projections.TopProductProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // --- METODO PARA DASHBOARD ---

    // Tabla 2: Top Productos
    @Query("SELECT oi.productName as productName, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "WHERE oi.order.createdAt >= :startDate AND oi.order.status <> 'CANCELADO' " +
            "GROUP BY oi.productName " +
            "ORDER BY totalQuantity DESC")
    List<TopProductProjection> findTopSellingProducts(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}