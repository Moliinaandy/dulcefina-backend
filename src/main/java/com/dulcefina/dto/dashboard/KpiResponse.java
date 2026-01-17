package com.dulcefina.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KpiResponse {
    private Double monthlyRevenue;
    private Long monthlyOrders;
    private Long pendingOrders;
    private Long newCustomers;
}