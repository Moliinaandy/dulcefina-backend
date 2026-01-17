package com.dulcefina.controller;

import com.dulcefina.dto.CheckoutRequest;
import com.dulcefina.dto.UpdateOrderStatusRequest;
import com.dulcefina.entity.Order;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserAccountRepository userRepo;

    public OrderController(OrderService orderService, UserAccountRepository userRepo) {
        this.orderService = orderService;
        this.userRepo = userRepo;
    }

    // --- Helper ---
    private UserAccount fetchUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    // --- ENDPOINTS PARA CLIENTES ---

    /** Endpoint para crear un nuevo pedido a partir del carrito de un usuario. */
    @PostMapping("/{userId}/create")
    public ResponseEntity<?> createOrder(@PathVariable Long userId, @RequestBody CheckoutRequest request) {
        try {
            UserAccount user = fetchUser(userId);
            Order newOrder = orderService.createOrderFromCart(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
        } catch (NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** Endpoint para que un usuario vea su propio historial de pedidos.*/
    @GetMapping("/{userId}/my-orders")
    public ResponseEntity<?> getMyOrders(@PathVariable Long userId) {
        try {
            UserAccount user = fetchUser(userId);
            List<Order> orders = orderService.getOrdersByUser(user);
            return ResponseEntity.ok(orders);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Endpoint para que un usuario vea un pedido específico. El servicio debe validar que el pedido pertenezca al usuario. */
    @GetMapping("/{userId}/order/{orderId}")
    public ResponseEntity<?> getUserOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            UserAccount user = fetchUser(userId);
            Order order = orderService.getUserOrderById(user, orderId);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            // --- CORRECCIÓN ---
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // --- ENDPOINTS PARA ADMIN ---

    /** [ADMIN] Obtiene todos los pedidos de todos los usuarios. */
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /** [ADMIN] Obtiene un pedido específico por su ID. */
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<?> getOrderByIdAsAdmin(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** [ADMIN] Actualiza el estado de un pedido. */
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}