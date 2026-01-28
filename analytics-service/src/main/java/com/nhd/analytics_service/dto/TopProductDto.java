package com.nhd.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;

    public static TopProductDto from(Object[] row) {
        return new TopProductDto(
                (Long) row[0],
                (String) row[1],
                (Long) row[2],
                (BigDecimal) row[3],
                (BigDecimal) row[4]
        );
    }
}
