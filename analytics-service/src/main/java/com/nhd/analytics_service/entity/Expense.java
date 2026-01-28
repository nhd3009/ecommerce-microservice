package com.nhd.analytics_service.entity;

import com.nhd.analytics_service.enums.ExpenseType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private BigDecimal amount;

    private ExpenseType type;

    private LocalDate expenseDate;

    private String note;
}