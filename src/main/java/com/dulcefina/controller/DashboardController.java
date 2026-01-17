package com.dulcefina.controller;

import com.dulcefina.dto.dashboard.KpiResponse;
import com.dulcefina.entity.Product;
import com.dulcefina.repository.projections.DailySaleProjection;
import com.dulcefina.repository.projections.OrderStatusProjection;
import com.dulcefina.repository.projections.TopProductProjection;
import com.dulcefina.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<KpiResponse> getKpis() {
        return ResponseEntity.ok(dashboardService.getKpis());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(dashboardService.getLowStockProducts());
    }

    @GetMapping("/daily-sales")
    public ResponseEntity<List<DailySaleProjection>> getDailySales(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(dashboardService.getDailySales(days));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductProjection>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts(limit));
    }

    @GetMapping("/order-status-counts")
    public ResponseEntity<List<OrderStatusProjection>> getActiveOrderStatusCounts() {
        return ResponseEntity.ok(dashboardService.getActiveOrderStatusCounts());
    }
}