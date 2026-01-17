package com.dulcefina.repository;

import com.dulcefina.entity.ProductionItem;
import com.dulcefina.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionItemRepository extends JpaRepository<ProductionItem, Long> {
    List<ProductionItem> findByProductionOrder(ProductionOrder productionOrder);
}
