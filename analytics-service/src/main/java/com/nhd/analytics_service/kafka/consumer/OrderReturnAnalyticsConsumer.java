package com.nhd.analytics_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nhd.analytics_service.service.AnalyticsAggregationService;
import com.nhd.commonlib.event.order_analytics.OrderReturnAnalyticsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderReturnAnalyticsConsumer {
    private final AnalyticsAggregationService aggregationService;

    @KafkaListener(
            topics = "order.returned.analytics",
            groupId = "analytics-service-group",
            containerFactory = "analyticsOrderReturnListenerContainerFactory"
    )
    public void consume(OrderReturnAnalyticsEvent event) {
        log.info("[KAFKA] Received OrderCompletedEvent orderId={}", event.getOrderId());

        aggregationService.recordReturn(event);
    }
}
