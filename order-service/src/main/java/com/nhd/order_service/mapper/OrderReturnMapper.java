package com.nhd.order_service.mapper;

import com.nhd.order_service.dto.OrderReturnDto;
import com.nhd.order_service.entity.OrderReturn;

public class OrderReturnMapper {

    public static OrderReturnDto toDto(OrderReturn orderReturn){
        return OrderReturnDto.builder()
                .id(orderReturn.getId())
                .orderId(orderReturn.getOrderId())
                .orderItemId(orderReturn.getOrderItemId())
                .quantity(orderReturn.getQuantity())
                .refundAmount(orderReturn.getRefundAmount())
                .returnedReason(orderReturn.getReturnedReason())
                .rejectedReason(orderReturn.getRejectedReason())
                .status(orderReturn.getStatus())
                .createdAt(orderReturn.getCreatedAt())
                .build();
    }
}
