package com.nhd.commonlib.event.order_analytics;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderCompletedEvent {

    private Long orderId;
    private LocalDate orderDate;
    private List<SaleItemDto> items;
}
