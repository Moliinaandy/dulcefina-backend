package com.dulcefina.service.impl;

import com.dulcefina.dto.CreateSessionRequest;
import com.dulcefina.dto.ConfirmSessionRequest;
import com.dulcefina.entity.*;
import com.dulcefina.repository.*;
import com.dulcefina.service.CartService;
import com.dulcefina.service.EmailService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.model.PaymentIntent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StripeService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final EmailService emailService;
    private final UserAccountRepository userAccountRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Value("${stripe.currency:pen}")
    private String currency;

    public StripeService(OrderRepository orderRepository,
                         ProductRepository productRepository,
                         CartService cartService,
                         EmailService emailService,
                         UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.emailService = emailService;
        this.userAccountRepository = userAccountRepository;
    }
    public String createPaymentIntent(Double amount) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        long amountInCents = Math.round(amount * 100);

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency(currency)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return paymentIntent.getClientSecret();
    }

    public Map<String, String> createCheckoutSession(CreateSessionRequest req) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        long amountInCents = Math.round(req.getAmount() * 100);

        // Construir metadata con info del checkout
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", String.valueOf(req.getUserId()));
        if (req.getCheckoutData() != null) {
            Object name = req.getCheckoutData().get("customerName");
            if (name != null) metadata.put("customerName", String.valueOf(name));
            Object phone = req.getCheckoutData().get("customerPhone");
            if (phone != null) metadata.put("customerPhone", String.valueOf(phone));
            Object addr = req.getCheckoutData().get("shippingAddress");
            if (addr != null) metadata.put("shippingAddress", String.valueOf(addr));
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Pedido Dulcefina")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency)
                        .setUnitAmount(amountInCents)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem item = SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(1L)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(item)
                .putAllMetadata(metadata)
                .build();

        Session session = Session.create(params);

        Map<String, String> resp = new HashMap<>();
        resp.put("id", session.getId());
        resp.put("url", session.getUrl() != null ? session.getUrl() : "");
        return resp;
    }

    @Transactional
    public Order confirmSessionAndCreateOrder(ConfirmSessionRequest req) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        Session session = Session.retrieve(req.getSessionId());

        if (!"paid".equals(session.getPaymentStatus())) {
            throw new IllegalStateException("Payment not completed: " + session.getPaymentStatus());
        }

        Map<String, String> meta = session.getMetadata();
        Long userId = null;
        if (meta != null && meta.get("userId") != null) {
            userId = Long.parseLong(meta.get("userId"));
        } else {
            userId = req.getUserId();
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        com.dulcefina.dto.CheckoutRequest checkoutRequest = new com.dulcefina.dto.CheckoutRequest();
        if (meta != null) {
            checkoutRequest.setCustomerName(meta.getOrDefault("customerName", user.getFullName()));
            checkoutRequest.setCustomerPhone(meta.getOrDefault("customerPhone", user.getPhone()));
            checkoutRequest.setShippingAddress(meta.getOrDefault("shippingAddress", ""));
            checkoutRequest.setPaymentMethod("TARJETA");
        }

        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Stock insuficiente para: " + product.getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PAGADO");
        order.setPaymentStatus("APROBADO");
        order.setPaymentMethod("STRIPE_CHECKOUT");
        order.setCustomerName(checkoutRequest.getCustomerName());
        order.setShippingAddress(checkoutRequest.getShippingAddress());
        order.setCustomerPhone(checkoutRequest.getCustomerPhone());

        double total = cartService.calculateCartSubtotal(cart);
        order.setTotalPrice(total);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setProductName(product.getName());
            oi.setQuantity(cartItem.getQuantity());
            oi.setUnitPrice(cartItem.getUnitPrice());
            oi.setSubtotal(cartItem.getSubtotal());
            oi.setCustomization(cartItem.getCustomization());
            orderItems.add(oi);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);
        try {
            emailService.sendOrderConfirmation(user.getEmail(), savedOrder);
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }

        return savedOrder;
    }
}
