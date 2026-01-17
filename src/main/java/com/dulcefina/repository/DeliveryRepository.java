package com.dulcefina.repository;

import com.dulcefina.entity.Delivery;
import com.dulcefina.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrder(Order order);
    List<Delivery> findByStatus(String status);
}
