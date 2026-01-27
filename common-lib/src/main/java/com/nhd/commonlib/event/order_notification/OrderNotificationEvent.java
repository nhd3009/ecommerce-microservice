package com.nhd.commonlib.event.order_notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNotificationEvent {
    private Long orderId;
    private Long userId;
    private String email;
    private String message;
    private String status;
    private List<OrderItemEvent> items;
    private BigDecimal totalAmount;
    private Instant timestamp;
}
