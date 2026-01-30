package com.nhd.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class DailyRevenueDto {
    private LocalDate date;

    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;
    private BigDecimal netProfit;
    private BigDecimal totalExpense;


    private Long totalItemReturned;
    private BigDecimal totalRefundAmount;

    private Long totalOrders;
    private Long totalItemsSold;
}
