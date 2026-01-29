package com.nhd.analytics_service.service;

import com.nhd.analytics_service.dto.ExpenseDto;
import com.nhd.analytics_service.entity.DailyRevenue;
import com.nhd.analytics_service.entity.Expense;
import com.nhd.analytics_service.repository.DailyRevenueRepository;
import com.nhd.analytics_service.repository.ExpenseRepository;
import com.nhd.commonlib.exception.BadRequestException;
import com.nhd.commonlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhd.analytics_service.request.ExpenseRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final DailyRevenueRepository dailyRepo;

    @Transactional
    public ExpenseDto addExpense(ExpenseRequest req) {

        Expense expense = Expense.builder()
                .expenseDate(req.getDate())
                .name(req.getName())
                .amount(req.getAmount())
                .type(req.getType())
                .note(req.getNote())
                .build();

        expenseRepo.save(expense);

        applyExpenseToDailyRevenue(expense);
        return ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .type(expense.getType())
                .expenseDate(expense.getExpenseDate())
                .note(expense.getNote())
                .build();
    }

    @Transactional
    public ExpenseDto updateExpense(Long expenseId, ExpenseRequest request) {

        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        BigDecimal oldAmount = expense.getAmount();
        LocalDate oldDate = expense.getExpenseDate();

        expense.setName(request.getName());
        expense.setAmount(request.getAmount());
        expense.setType(request.getType());
        expense.setNote(request.getNote());
        expense.setExpenseDate(request.getDate());

        expenseRepo.save(expense);

        if (oldDate.equals(request.getDate())) {
            BigDecimal delta = request.getAmount().subtract(oldAmount);
            updateDailyExpense(oldDate, delta);
        }
        else {
            updateDailyExpense(oldDate, oldAmount.negate());
            updateDailyExpense(request.getDate(), request.getAmount());
        }

        return ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .type(expense.getType())
                .expenseDate(expense.getExpenseDate())
                .note(expense.getNote())
                .build();
    }

    @Transactional
    public void deleteExpense(Long expenseId) {

        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        BigDecimal amount = expense.getAmount();
        LocalDate date = expense.getExpenseDate();

        expenseRepo.delete(expense);

        updateDailyExpense(date, amount.negate());
    }

    public List<ExpenseDto> getExpenses(LocalDate from, LocalDate to) {

        if (from == null || to == null) {
            throw new BadRequestException("from and to must not be null");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("from must be before or equal to to");
        }

        return expenseRepo
                .findByExpenseDateBetweenOrderByExpenseDateAsc(from, to)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ExpenseDto toDto(Expense e) {
        return ExpenseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .expenseDate(e.getExpenseDate())
                .amount(e.getAmount())
                .type(e.getType())
                .note(e.getNote())
                .build();
    }

    private void applyExpenseToDailyRevenue(Expense expense) {
        DailyRevenue daily = dailyRepo.findById(expense.getExpenseDate())
                .orElseGet(() -> DailyRevenue.builder()
                        .date(expense.getExpenseDate())
                        .totalRevenue(BigDecimal.ZERO)
                        .totalCost(BigDecimal.ZERO)
                        .totalProfit(BigDecimal.ZERO)
                        .totalExpense(BigDecimal.ZERO)
                        .netProfit(BigDecimal.ZERO)
                        .totalOrders(0L)
                        .totalItemsSold(0L)
                        .build()
                );

        daily.setTotalExpense(
                daily.getTotalExpense().add(expense.getAmount())
        );

        daily.setNetProfit(
                daily.getTotalProfit().subtract(daily.getTotalExpense())
        );

        dailyRepo.save(daily);
    }

    private void updateDailyExpense(LocalDate date, BigDecimal delta) {
        DailyRevenue daily = dailyRepo.findById(date)
                .orElseGet(() -> DailyRevenue.builder()
                        .date(date)
                        .totalExpense(BigDecimal.ZERO)
                        .totalRevenue(BigDecimal.ZERO)
                        .totalCost(BigDecimal.ZERO)
                        .totalProfit(BigDecimal.ZERO)
                        .totalOrders(0L)
                        .totalItemsSold(0L)
                        .netProfit(BigDecimal.ZERO)
                        .build());

        daily.setTotalExpense(daily.getTotalExpense().add(delta));

        daily.setNetProfit(
                daily.getTotalProfit()
                        .subtract(daily.getTotalExpense())
        );

        dailyRepo.save(daily);
    }
}
