package com.nhd.product_service.request;

import java.math.BigDecimal;

import com.nhd.product_service.enums.ProductStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFilterRequest {
    private String name;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ProductStatus status;
    private int page;
    private int size;
}
