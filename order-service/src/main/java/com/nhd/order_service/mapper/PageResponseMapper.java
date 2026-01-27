package com.nhd.order_service.mapper;

import com.nhd.commonlib.response.PageResponse;
import org.springframework.data.domain.Page;

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
