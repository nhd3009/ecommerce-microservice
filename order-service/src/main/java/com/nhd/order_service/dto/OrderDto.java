package com.nhd.order_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.nhd.order_service.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private String orderEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String note;
    private String recipientPhone;
    private String shippingAddress;
    private String recipientName;
    private String deliveryProvider;
    private String trackingNumber;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDto> items;
}
