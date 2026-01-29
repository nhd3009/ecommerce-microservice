package com.nhd.commonlib.event.order_notification;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReturnApprovedEvent {
    private Long returnId;
    private Long orderId;
    private Long userId;
    private String email;
    private String productName;
    private Integer quantity;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String receiver;
    private String note;
    private Instant approvedAt;
}
