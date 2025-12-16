package com.nhd.product_service.request;

import com.nhd.product_service.enums.CategoryStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotNull
    private String name;
    private String description;
    private CategoryStatus status;
}
