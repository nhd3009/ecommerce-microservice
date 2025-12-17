package com.nhd.product_service.request;

import com.nhd.product_service.enums.CategoryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {
    private String name;
    private String description;
    private CategoryStatus status;
}