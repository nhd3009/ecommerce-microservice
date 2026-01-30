package com.nhd.commonlib.event.order_analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReturnCompletedEvent {
    private Long returnId;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;

    private Integer quantity;

    private BigDecimal refundRevenue;
    private BigDecimal refundCost;
    private BigDecimal refundProfit;

    private LocalDate returnDate;
}