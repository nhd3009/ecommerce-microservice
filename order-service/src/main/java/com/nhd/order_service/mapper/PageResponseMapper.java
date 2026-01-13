package com.nhd.order_service.mapper;

import org.springframework.data.domain.Page;

import com.nhd.order_service.response.PageResponse;

public class PageResponseMapper {
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .data(page.getContent())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
