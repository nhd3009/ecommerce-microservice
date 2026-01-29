package com.nhd.analytics_service.request;

import com.nhd.analytics_service.enums.ExpenseType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {
    private LocalDate date;
    private String name;
    private BigDecimal amount;
    private ExpenseType type;
    private String note;
}
