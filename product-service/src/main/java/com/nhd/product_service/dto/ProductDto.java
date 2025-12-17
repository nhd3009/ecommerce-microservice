package com.nhd.product_service.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stockQuantity;
  private String thumbnailUrl;
  private List<String> imageUrls;
}
