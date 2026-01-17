package com.dulcefina.service.impl;

import com.dulcefina.dto.dashboard.KpiResponse;
import com.dulcefina.entity.Product;
import com.dulcefina.repository.*;
import com.dulcefina.repository.projections.DailySaleProjection;
import com.dulcefina.repository.projections.OrderStatusProjection;
import com.dulcefina.repository.projections.TopProductProjection;
import com.dulcefina.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public DashboardServiceImpl(OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository,
                                ProductRepository productRepository,
                                UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public KpiResponse getKpis() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        Double revenue = orderRepository.findMonthlyRevenue(monthStart);
        Long orders = orderRepository.countMonthlyOrders(monthStart);
        Long pending = orderRepository.countPendingOrders();
        Long newUsers = userAccountRepository.countNewUsers(monthStart);

        return KpiResponse.builder()
                .monthlyRevenue(Optional.ofNullable(revenue).orElse(0.0))
                .monthlyOrders(Optional.ofNullable(orders).orElse(0L))
                .pendingOrders(Optional.ofNullable(pending).orElse(0L))
                .newCustomers(Optional.ofNullable(newUsers).orElse(0L))
                .build();
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findProductsWithLowStock();
    }

    @Override
    public List<DailySaleProjection> getDailySales(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return orderRepository.findDailySales(startDate);
    }

    @Override
    public List<TopProductProjection> getTopSellingProducts(int limit) {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Pageable pageable = PageRequest.of(0, limit);
        return orderItemRepository.findTopSellingProducts(monthStart, pageable);
    }

    @Override
    public List<OrderStatusProjection> getActiveOrderStatusCounts() {
        return orderRepository.countActiveOrdersByStatus();
    }
}