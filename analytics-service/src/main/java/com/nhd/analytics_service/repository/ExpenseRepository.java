package com.nhd.analytics_service.repository;

import com.nhd.analytics_service.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.expenseDate BETWEEN :from AND :to
    """)
    BigDecimal getTotalExpense(LocalDate from, LocalDate to);

    @Query("""
        SELECT e.type, SUM(e.amount)
        FROM Expense e
        WHERE e.expenseDate BETWEEN :from AND :to
        GROUP BY e.type
    """)
    List<Object[]> getExpenseByType(LocalDate from, LocalDate to);

    List<Expense> findByExpenseDateBetweenOrderByExpenseDateAsc(LocalDate from, LocalDate to);
}
