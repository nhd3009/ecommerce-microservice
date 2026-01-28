package com.nhd.analytics_service.service;

import com.nhd.analytics_service.entity.DailyRevenue;
import com.nhd.analytics_service.entity.ProcessedOrder;
import com.nhd.analytics_service.entity.SalesTransaction;
import com.nhd.analytics_service.repository.DailyRevenueRepository;
import com.nhd.analytics_service.repository.ProcessedOrderRepository;
import com.nhd.analytics_service.repository.SalesTransactionRepository;
import com.nhd.commonlib.event.order_analytics.OrderCompletedEvent;
import com.nhd.commonlib.event.order_analytics.SaleItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsAggregationService {
    private final SalesTransactionRepository salesRepo;
    private final DailyRevenueRepository dailyRepo;
    private final ProcessedOrderRepository processedOrderRepo;

    @Transactional
    public void recordSale(OrderCompletedEvent event) {
        try {
            processedOrderRepo.save(
                    new ProcessedOrder(event.getOrderId(), Instant.now())
            );
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate OrderCompletedEvent {}", event.getOrderId());
            return;
        }
        LocalDate date = event.getOrderDate();

        BigDecimal revenueSum = BigDecimal.ZERO;
        BigDecimal costSum = BigDecimal.ZERO;
        BigDecimal profitSum = BigDecimal.ZERO;
        long itemCount = 0;

        for (SaleItemDto item : event.getItems()) {

            BigDecimal revenue = item.getSellPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal cost = item.getImportPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal profit = revenue.subtract(cost);

            salesRepo.save(SalesTransaction.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .categoryId(item.getCategoryId())
                    .productName(item.getProductName())
                    .categoryName(item.getCategoryName())
                    .quantity(item.getQuantity())
                    .sellPrice(item.getSellPrice())
                    .importPrice(item.getImportPrice())
                    .revenue(revenue)
                    .cost(cost)
                    .profit(profit)
                    .orderDate(date)
                    .build());

            revenueSum = revenueSum.add(revenue);
            costSum = costSum.add(cost);
            profitSum = profitSum.add(profit);
            itemCount += item.getQuantity();
        }

        DailyRevenue daily = dailyRepo.findById(date)
                .orElseGet(() -> DailyRevenue.builder()
                        .date(date)
                        .totalRevenue(BigDecimal.ZERO)
                        .totalCost(BigDecimal.ZERO)
                        .totalProfit(BigDecimal.ZERO)
                        .totalOrders(0L)
                        .totalItemsSold(0L)
                        .totalExpense(BigDecimal.ZERO)
                        .netProfit(BigDecimal.ZERO)
                        .build());

        daily.setTotalRevenue(daily.getTotalRevenue().add(revenueSum));
        daily.setTotalCost(daily.getTotalCost().add(costSum));
        daily.setTotalProfit(daily.getTotalProfit().add(profitSum));
        daily.setTotalOrders(daily.getTotalOrders() + 1);
        daily.setTotalItemsSold(daily.getTotalItemsSold() + itemCount);

        dailyRepo.save(daily);
    }
}

