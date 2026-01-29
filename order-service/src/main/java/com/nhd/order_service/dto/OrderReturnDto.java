package com.nhd.order_service.dto;

import com.nhd.order_service.enums.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderReturnDto {

    private Long id;
    private Long orderId;
    private Long orderItemId;
    private String returnedReason;
    private String rejectedReason;
    private Integer quantity;
    private BigDecimal refundAmount;
    private ReturnStatus status;
    private Instant createdAt;
}