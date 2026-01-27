package com.nhd.product_service.request;

import java.math.BigDecimal;
import java.util.List;


import com.nhd.commonlib.dto.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private ProductStatus status;
    private String thumbnailUrl;
    private List<String> imageUrls;
}
