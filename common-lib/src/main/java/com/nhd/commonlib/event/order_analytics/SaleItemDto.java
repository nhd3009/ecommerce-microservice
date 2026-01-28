package com.nhd.commonlib.event.order_analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDto {

    private Long orderItemId;

    private Long productId;
    private Long categoryId;

    private String productName;
    private String categoryName;

    private Integer quantity;

    private BigDecimal sellPrice;
    private BigDecimal importPrice;
}