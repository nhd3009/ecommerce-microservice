package com.nhd.product_service.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nhd.product_service.enums.CategoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "id", "name", "description", "status", "createdAt", "updatedAt" })
public class CategoryDto {
  private Long id;
  private String name;
  private String description;
  private CategoryStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
