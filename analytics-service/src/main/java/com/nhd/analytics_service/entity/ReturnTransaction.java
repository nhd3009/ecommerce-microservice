package com.nhd.analytics_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "return_transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnTransaction {

    @Id
    @GeneratedValue
    private Long id;

    private Long returnId;
    private Long orderId;
    private Long orderItemId;

    private Long productId;
    private Long categoryId;

    private Integer quantity;

    private BigDecimal refundRevenue;
    private BigDecimal refundCost;
    private BigDecimal refundProfit;

    private LocalDate returnDate;

    private Instant createdAt;
}
