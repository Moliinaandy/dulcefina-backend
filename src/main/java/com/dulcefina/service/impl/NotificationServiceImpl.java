package com.dulcefina.service.impl;

import com.dulcefina.dto.NotificationDTO;
import com.dulcefina.entity.Order;
import com.dulcefina.entity.Product;
import com.dulcefina.repository.OrderRepository;
import com.dulcefina.repository.ProductRepository;
import com.dulcefina.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public NotificationServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications() {
        List<NotificationDTO> notifications = new ArrayList<>();

        List<Order> pendingOrders = orderRepository.findByStatusAndNotificationReadFalse("PENDIENTE");
        for (Order order : pendingOrders) {
            notifications.add(new NotificationDTO(
                    "order-" + order.getOrderId(),
                    "NEW_ORDER",
                    "Nuevo pedido (#" + order.getOrderId() + ") de " + order.getCustomerName(),
                    order.getCreatedAt()
            ));
        }

        List<Product> lowStockProducts = productRepository.findProductsWithLowStockNotNotified();
        for (Product product : lowStockProducts) {
            notifications.add(new NotificationDTO(
                    "product-" + product.getProductId(),
                    "LOW_STOCK",
                    "Stock bajo para " + product.getName() + " (Actual: " + product.getStock() + ")",
                    LocalDateTime.now()
            ));
        }

        notifications.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

        return notifications;
    }

    @Override
    public void markAsRead(List<String> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }

        for (String id : notificationIds) {
            try {
                if (id.startsWith("order-")) {
                    Long orderId = Long.parseLong(id.substring(6));
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + orderId));
                    order.setNotificationRead(true);
                    orderRepository.save(order);

                } else if (id.startsWith("product-")) {
                    Long productId = Long.parseLong(id.substring(8));
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productId));
                    product.setLowStockNotified(true);
                    productRepository.save(product);
                }
            } catch (Exception e) {
                System.err.println("Error al marcar notificación como leída: " + id + ". Error: " + e.getMessage());
            }
        }
    }
}