package com.nhd.analytics_service.controller;

import com.nhd.analytics_service.dto.ExpenseDto;
import com.nhd.analytics_service.request.CreateExpenseRequest;
import com.nhd.analytics_service.service.ExpenseService;
import com.nhd.commonlib.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ApiResponse<ExpenseDto> create(@RequestBody @Valid CreateExpenseRequest request) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Expense created successfully",
                expenseService.addExpense(request)
        );
    }

    @GetMapping
    public ApiResponse<List<ExpenseDto>> getByDateRange(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Expenses retrieved successfully",
                expenseService.getExpenses(from, to)
        );
    }
}

