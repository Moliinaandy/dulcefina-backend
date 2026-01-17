package com.dulcefina.repository;

import com.dulcefina.entity.Order;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.projections.DailySaleProjection;
import com.dulcefina.repository.projections.OrderStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(UserAccount user);
    Optional<Order> findByOrderIdAndUser(Long orderId, UserAccount user);

    // --- METODOS PARA DASHBOARD ---

    // KPI 1: Ingresos del Mes
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt >= :startDate AND o.status <> 'CANCELADO'")
    Double findMonthlyRevenue(@Param("startDate") LocalDateTime startDate);

    // KPI 2: Pedidos del Mes
    @Query("SELECT COUNT(o.orderId) FROM Order o WHERE o.createdAt >= :startDate")
    Long countMonthlyOrders(@Param("startDate") LocalDateTime startDate);

    // KPI 3: Pedidos Pendientes
    @Query("SELECT COUNT(o.orderId) FROM Order o WHERE o.status = 'PENDIENTE'")
    Long countPendingOrders();

    // Gráfico 1: Ventas Diarias
    @Query("SELECT FUNCTION('DATE', o.createdAt) as date, SUM(o.totalPrice) as total " +
            "FROM Order o " +
            "WHERE o.createdAt >= :startDate AND o.status <> 'CANCELADO' " +
            "GROUP BY FUNCTION('DATE', o.createdAt) " +
            "ORDER BY date ASC")
    List<DailySaleProjection> findDailySales(@Param("startDate") LocalDateTime startDate);

    // Gráfico 2: Conteo de Estados
    @Query("SELECT o.status as status, COUNT(o.orderId) as count " +
            "FROM Order o " +
            "WHERE o.status IN ('PENDIENTE', 'EN PREPARACIÓN', 'ENVIADO') " +
            "GROUP BY o.status")
    List<OrderStatusProjection> countActiveOrdersByStatus();

    List<Order> findByStatusAndNotificationReadFalse(String status);
}