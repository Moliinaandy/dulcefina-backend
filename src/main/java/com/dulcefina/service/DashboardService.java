package com.dulcefina.service;

import com.dulcefina.dto.dashboard.KpiResponse;
import com.dulcefina.entity.Product;
import com.dulcefina.repository.projections.DailySaleProjection;
import com.dulcefina.repository.projections.OrderStatusProjection;
import com.dulcefina.repository.projections.TopProductProjection;

import java.util.List;

public interface DashboardService {
    KpiResponse getKpis();
    List<Product> getLowStockProducts();
    List<DailySaleProjection> getDailySales(int days);
    List<TopProductProjection> getTopSellingProducts(int limit);
    List<OrderStatusProjection> getActiveOrderStatusCounts();
}