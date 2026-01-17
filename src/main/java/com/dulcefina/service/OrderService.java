package com.dulcefina.service;

import com.dulcefina.dto.CheckoutRequest;
import com.dulcefina.entity.Order;
import com.dulcefina.entity.UserAccount;

import java.util.List;
import java.util.NoSuchElementException;

public interface OrderService {

    // --- Metodos para Clientes ---

    /** Crea un nuevo pedido a partir del carrito de un usuario y limpia el carrito. */
    Order createOrderFromCart(UserAccount user, CheckoutRequest request);

    /** Obtiene el historial de pedidos de un usuario. */
    List<Order> getOrdersByUser(UserAccount user);

    /** Obtiene un pedido específico de un usuario, validando que le pertenezca. */
    Order getUserOrderById(UserAccount user, Long orderId) throws NoSuchElementException, SecurityException;

    // --- Metodos para Admin ---

    /** Obtiene todos los pedidos del sistema */
    List<Order> getAllOrders();

    /** Obtiene cualquier pedido por su ID */
    Order getOrderById(Long orderId) throws NoSuchElementException;

    /** Actualiza el estado de un pedido (ej: "PENDIENTE" -> "ENVIADO") */
    Order updateOrderStatus(Long orderId, String newStatus) throws NoSuchElementException, IllegalArgumentException;
}