package com.nhd.product_service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.nhd.commonlib.dto.ProductDto;
import com.nhd.product_service.entity.Product;
import com.nhd.product_service.entity.ProductImage;

public class ProductMapper {
    public static ProductDto toDto(Product product) {
        List<String> imageUrls = product.getImages() != null
                ? product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .thumbnailUrl(product.getThumbnailUrl())
                .imageUrls(imageUrls)
                .categoryName(product.getCategory() != null
                        ? product.getCategory().getName()
                        : null)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
