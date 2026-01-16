package com.nhd.order_service.mapper;

import java.util.List;

import com.nhd.order_service.dto.OrderDto;
import com.nhd.order_service.dto.OrderItemDto;
import com.nhd.order_service.entity.Order;
import com.nhd.order_service.entity.OrderItem;

public class OrderMapper {
    public static OrderItemDto toOrderItemDto(OrderItem item) {
        return new OrderItemDto(
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubTotal()
        );
    }

    public static OrderDto toOrderDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(OrderMapper::toOrderItemDto)
                .toList();

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .recipientPhone(order.getRecipientPhone())
                .shippingAddress(order.getShippingAddress())
                .recipientName(order.getRecipientName())
                .deliveryProvider(order.getDeliveryProvider())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDtos)
                .build();
    }
}
