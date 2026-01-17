package com.dulcefina.repository;

import com.dulcefina.entity.PurchaseOrder;
import com.dulcefina.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplier(Supplier supplier);
}
