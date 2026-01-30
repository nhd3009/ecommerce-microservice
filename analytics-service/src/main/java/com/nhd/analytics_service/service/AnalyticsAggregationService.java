package com.nhd.analytics_service.service;

import com.nhd.analytics_service.entity.DailyRevenue;
import com.nhd.analytics_service.entity.ProcessedOrder;
import com.nhd.analytics_service.entity.ProcessedReturn;
import com.nhd.analytics_service.entity.ReturnTransaction;
import com.nhd.analytics_service.entity.SalesTransaction;
import com.nhd.analytics_service.repository.DailyRevenueRepository;
import com.nhd.analytics_service.repository.ProcessedOrderRepository;
import com.nhd.analytics_service.repository.ProcessedReturnRepository;
import com.nhd.analytics_service.repository.ReturnTransactionRepository;
import com.nhd.analytics_service.repository.SalesTransactionRepository;
import com.nhd.commonlib.event.order_analytics.OrderCompletedEvent;
import com.nhd.commonlib.event.order_analytics.OrderReturnAnalyticsEvent;
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
    private final ProcessedReturnRepository processedReturnRepo;
    private final ReturnTransactionRepository returnTransactionRepo;

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
                        .totalItemReturned(0L)
                        .totalRefundAmount(BigDecimal.ZERO)
                        .totalExpense(BigDecimal.ZERO)
                        .netProfit(BigDecimal.ZERO)
                        .build());

        daily.setTotalRevenue(daily.getTotalRevenue().add(revenueSum));
        daily.setTotalCost(daily.getTotalCost().add(costSum));
        daily.setTotalProfit(daily.getTotalProfit().add(profitSum));
        daily.setTotalOrders(daily.getTotalOrders() + 1);
        daily.setTotalItemsSold(daily.getTotalItemsSold() + itemCount);
        daily.setNetProfit(
            daily.getTotalProfit()
                .subtract(daily.getTotalExpense())
        );
        dailyRepo.save(daily);
    }

    @Transactional
    public void recordReturn(OrderReturnAnalyticsEvent event) {

        try {
            processedReturnRepo.save(
                    new ProcessedReturn(event.getReturnId(), Instant.now())
            );
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate return event {}", event.getReturnId());
            return;
        }

        returnTransactionRepo.save(
            ReturnTransaction.builder()
                .returnId(event.getReturnId())
                .orderId(event.getOrderId())
                .orderItemId(event.getOrderItemId())
                .productId(event.getProductId())
                .productName(event.getProductName())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .quantity(event.getQuantity())
                .refundRevenue(event.getRefundRevenue())
                .refundCost(event.getRefundCost())
                .refundProfit(event.getRefundProfit())
                .returnDate(event.getReturnDate())
                .createdAt(Instant.now())
                .build()
        );

        DailyRevenue daily = dailyRepo.findById(event.getReturnDate())
                .orElseThrow(() ->
                    new IllegalStateException("DailyRevenue not found for date " + event.getReturnDate())
                );

        daily.setTotalRefundAmount(
                daily.getTotalRefundAmount().add(event.getRefundRevenue())
        );

        daily.setTotalItemReturned(
                daily.getTotalItemReturned() + event.getQuantity()
        );

        BigDecimal adjustedProfit =
                daily.getTotalProfit().subtract(event.getRefundProfit());

        daily.setTotalProfit(adjustedProfit);

        daily.setNetProfit(
                adjustedProfit.subtract(daily.getTotalExpense())
        );

        dailyRepo.save(daily);
    }
}

