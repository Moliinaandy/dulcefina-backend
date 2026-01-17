package com.dulcefina.repository;

import com.dulcefina.entity.ProductionOrder;
import com.dulcefina.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Optional<ProductionOrder> findByProductionCode(String productionCode);
    Optional<ProductionOrder> findByOrder(Order order);
    List<ProductionOrder> findByStatus(String status);
}
