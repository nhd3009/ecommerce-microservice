package com.nhd.product_service.dto;

import java.util.List;

import com.nhd.commonlib.dto.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategoryProductDto {
    private Long categoryId;
    private String categoryName;
    private List<ProductDto> products;
}
