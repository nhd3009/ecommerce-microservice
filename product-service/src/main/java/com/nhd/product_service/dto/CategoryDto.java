package com.nhd.product_service.dto;

import java.time.Instant;

import com.nhd.product_service.enums.CategoryStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
  private Long id;
  private String name;
  private String description;
  private CategoryStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
