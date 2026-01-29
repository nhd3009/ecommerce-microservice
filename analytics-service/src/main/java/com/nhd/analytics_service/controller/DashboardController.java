package com.nhd.analytics_service.controller;

import com.nhd.analytics_service.dto.DailyRevenueDto;
import com.nhd.analytics_service.dto.DashboardSummaryDto;
import com.nhd.analytics_service.dto.TopProductDto;
import com.nhd.analytics_service.service.DashboardQueryService;
import com.nhd.commonlib.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardSummaryDto> dashboard(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        return new ApiResponse<>(HttpStatus.OK.value(), "Dashboard retrieved successfully!", dashboardQueryService.getSummary(from, to));
    }

    @GetMapping("/revenue/daily")
    public ApiResponse<List<DailyRevenueDto>> dailyRevenue(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        return new ApiResponse<>(HttpStatus.OK.value(), "Daily Revenue retrieved successfully!", dashboardQueryService.getDailyRevenue(from, to));
    }

    @GetMapping("/top-products")
    public ApiResponse<List<TopProductDto>> topProducts(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "5") int limit) {

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Top product retrieved successfully!",
                dashboardQueryService.getTopProductsByProfit(from, to, limit)
        );
    }
}

