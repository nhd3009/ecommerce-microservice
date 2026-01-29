package com.nhd.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderView {
    private Long productId;
    private String productName;
    private BigDecimal sellPrice;
    private BigDecimal importPrice;
    private Long categoryId;
    private String categoryName;
    private int stockQuantity;
}
