package com.nhd.product_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CacheInvalidationEvent {
    private String source;
    private String type;
    private Long entityId;
    private LocalDateTime timestamp;
}