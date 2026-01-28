package com.nhd.analytics_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class DashboardSummaryDto {

    private BigDecimal totalRevenue;
    private BigDecimal totalCost;

    private BigDecimal grossProfit;
    private BigDecimal totalExpense;

    private BigDecimal netProfit;
    private boolean isProfitable;
}
