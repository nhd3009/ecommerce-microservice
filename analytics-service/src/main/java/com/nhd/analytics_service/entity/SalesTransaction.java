package com.nhd.analytics_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_transactions",
    indexes = {
        @Index(name = "idx_sales_order_date", columnList = "orderDate"),
        @Index(name = "idx_sales_product", columnList = "productId"),
        @Index(name = "idx_sales_category", columnList = "categoryId")
    }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long categoryId;

    private String productName;
    private String categoryName;

    private Integer quantity;

    private BigDecimal importPrice;
    private BigDecimal sellPrice;

    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;

    private LocalDate orderDate;
}
