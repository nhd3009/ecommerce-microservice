package com.nhd.order_service.entity;

import com.nhd.order_service.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_returns")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private String userEmail;

    private Integer quantity;
    private BigDecimal refundAmount;

    private String returnedReason;
    private String rejectedReason;

    @Embedded
    private RefundInfo refundInfo;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private Instant createdAt;
    private Instant completedAt;
}
