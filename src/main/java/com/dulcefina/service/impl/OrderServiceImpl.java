package com.dulcefina.service.impl;

import com.dulcefina.dto.CheckoutRequest;
import com.dulcefina.entity.*;
import com.dulcefina.repository.OrderRepository;
import com.dulcefina.repository.OrderItemRepository;
import com.dulcefina.repository.ProductRepository;
import com.dulcefina.service.CartService;
import com.dulcefina.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dulcefina.service.EmailService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartService cartService,
                            ProductRepository productRepository,
                            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Override
    public Order createOrderFromCart(UserAccount user, CheckoutRequest request) {
        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede crear un pedido con un carrito vacío.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDIENTE");
        order.setPaymentStatus("PENDIENTE");
        order.setCustomerName(request.getCustomerName());
        order.setShippingAddress(request.getShippingAddress());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotalPrice(cartService.calculateCartSubtotal(cart));

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();
            int quantityPurchased = cartItem.getQuantity();

            Integer stockBefore = product.getStock();
            Integer minStock = product.getMinStock();

            if (stockBefore == null || stockBefore < quantityPurchased) {
                throw new IllegalStateException("Stock insuficiente para: " + product.getName() +
                        ". Stock disponible: " + (stockBefore != null ? stockBefore : 0));
            }

            int stockAfter = stockBefore - quantityPurchased;
            product.setStock(stockAfter);

            if (minStock != null && stockBefore > minStock && stockAfter <= minStock) {
                System.out.println("DISPARADOR ALERTA INMEDIATA: Stock de " + product.getName() +
                        " cruzó el umbral (" + stockAfter + " <= " + minStock + ").");

                try {
                    emailService.sendImmediateLowStockAlert(product);
                } catch (Exception e) {
                    System.err.println("ALERTA: El pedido se creó, pero falló el envío de la ALERTA DE STOCK INMEDIATO para "
                            + product.getName() + ": " + e.getMessage());
                }
            }

            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setProductName(product.getName());
            oi.setQuantity(quantityPurchased);
            oi.setUnitPrice(cartItem.getUnitPrice());
            oi.setSubtotal(cartItem.getSubtotal());
            oi.setCustomization(cartItem.getCustomization());

            orderItems.add(oi);
        }
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);
        try {
            emailService.sendOrderConfirmation(savedOrder.getUser().getEmail(), savedOrder);
        } catch (Exception e) {
            System.err.println("ALERTA: El pedido #" + savedOrder.getOrderId() +
                    " se creó, pero falló el envío del correo de confirmación a " +
                    savedOrder.getUser().getEmail() + ": " + e.getMessage());
        }
        return savedOrder;
    }

    @Override
    public List<Order> getOrdersByUser(UserAccount user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Order getUserOrderById(UserAccount user, Long orderId) throws NoSuchElementException, SecurityException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + orderId));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Acceso denegado: Este pedido no le pertenece.");
        }
        return order;
    }

    // --- Metodos de Admin ---

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderId) throws NoSuchElementException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + orderId));
    }

    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) throws NoSuchElementException, IllegalArgumentException {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío.");
        }

        Order order = getOrderById(orderId);
        order.setStatus(newStatus.toUpperCase());
        return orderRepository.save(order);
    }
}