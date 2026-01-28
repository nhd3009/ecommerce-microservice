package com.nhd.commonlib.event.order_analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompletedEvent {

    private Long orderId;
    private Long userId;

    private LocalDate orderDate;

    private BigDecimal totalAmount;

    private List<SaleItemDto> items;
}
