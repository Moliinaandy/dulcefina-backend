package com.dulcefina.repository;

import com.dulcefina.entity.PurchaseItem;
import com.dulcefina.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
    List<PurchaseItem> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
