package com.nhd.analytics_service.dto;

import com.nhd.analytics_service.enums.ExpenseType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseDto {
    private Long id;

    private String name;

    private BigDecimal amount;

    private ExpenseType type;

    private LocalDate expenseDate;

    private String note;
}
