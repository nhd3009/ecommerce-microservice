package com.nhd.order_service.request;

import com.nhd.order_service.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private String deliveryProvider;
    private String trackingNumber; 
}
