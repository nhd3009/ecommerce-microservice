package com.nhd.order_service.request;

import com.nhd.order_service.entity.RefundInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderReturnRequest {
    private String email;
    private Long orderItemId;
    private Integer quantity;
    private String reason;
    private RefundInfo refundInfo;
    private String receiver;
}
