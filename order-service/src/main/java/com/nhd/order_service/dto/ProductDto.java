package com.nhd.order_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.nhd.order_service.enums.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private String categoryName;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
