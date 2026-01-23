package com.nhd.order_service.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private String note;
    private String orderEmail;
    private String recipientPhone;
    private String shippingAddress;
    private String recipientName;
}
