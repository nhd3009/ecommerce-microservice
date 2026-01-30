package com.nhd.analytics_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_revenue")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyRevenue {
    @Id
    private LocalDate date;

    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;

    private Long totalOrders;
    private Long totalItemsSold;
    private BigDecimal totalExpense;

    private Long totalItemReturned;
    private BigDecimal totalRefundAmount;

    private BigDecimal netProfit;
}
