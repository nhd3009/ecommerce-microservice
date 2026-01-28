package com.nhd.analytics_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nhd.analytics_service.dto.DailyRevenueDto;
import com.nhd.analytics_service.dto.TopProductDto;
import com.nhd.analytics_service.entity.DailyRevenue;
import com.nhd.analytics_service.repository.DailyRevenueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhd.analytics_service.dto.DashboardSummaryDto;
import com.nhd.analytics_service.repository.ExpenseRepository;
import com.nhd.analytics_service.repository.SalesTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardQueryService {
    private final SalesTransactionRepository salesRepo;
    private final ExpenseRepository expenseRepo;
    private final DailyRevenueRepository dailyRevenueRepository;

    public DashboardSummaryDto getSummary(LocalDate from, LocalDate to) {

        List<Object[]> rows = salesRepo.getSummary(from, to);
        Object[] s = rows.getFirst();

        BigDecimal revenue = s[0] != null ? (BigDecimal) s[0] : BigDecimal.ZERO;
        BigDecimal cost = s[1] != null ? (BigDecimal) s[1] : BigDecimal.ZERO;
        BigDecimal grossProfit = s[2] != null ? (BigDecimal) s[2] : BigDecimal.ZERO;
        BigDecimal expense = Optional
                .ofNullable(expenseRepo.getTotalExpense(from, to))
                .orElse(BigDecimal.ZERO);

        BigDecimal netProfit = grossProfit.subtract(expense);
        boolean isProfitable = netProfit.compareTo(BigDecimal.ZERO) > 0;

        return DashboardSummaryDto.builder()
                .totalRevenue(revenue)
                .totalCost(cost)
                .grossProfit(grossProfit)
                .totalExpense(expense)
                .netProfit(netProfit)
                .isProfitable(isProfitable)
                .build();
    }

    public List<DailyRevenueDto> getDailyRevenue(LocalDate from, LocalDate to){
        List<DailyRevenue> dailyRevenue = dailyRevenueRepository.findByDateBetweenOrderByDateAsc(from, to);
        return dailyRevenue.stream().map(dr -> DailyRevenueDto.builder()
                .date(dr.getDate())
                .totalRevenue(dr.getTotalRevenue())
                .totalCost(dr.getTotalCost())
                .totalProfit(dr.getTotalProfit())
                .totalOrders(dr.getTotalOrders())
                .totalItemsSold(dr.getTotalItemsSold())
                .totalExpense(dr.getTotalExpense())
                .netProfit(dr.getNetProfit())
                .build())
                .toList();
    }

    public List<TopProductDto> getTopProductsByProfit(LocalDate from, LocalDate to, int limit) {

        Pageable pageable = PageRequest.of(0, limit);

        List<Object[]> rows =
                salesRepo.getTopProductByProfit(from, to, pageable);

        return rows.stream()
                .map(TopProductDto::from)
                .toList();
    }
}
