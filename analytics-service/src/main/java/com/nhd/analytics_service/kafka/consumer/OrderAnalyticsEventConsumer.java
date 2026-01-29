package com.nhd.analytics_service.kafka.consumer;

import com.nhd.analytics_service.service.AnalyticsAggregationService;
import com.nhd.commonlib.event.order_analytics.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderAnalyticsEventConsumer {

    private final AnalyticsAggregationService aggregationService;

    @KafkaListener(
            topics = "order.completed",
            groupId = "analytics-service-group",
            containerFactory = "analyticsKafkaListenerContainerFactory"
    )
    public void consume(OrderCompletedEvent event) {
        log.info("[KAFKA] Received OrderCompletedEvent orderId={}", event.getOrderId());

        aggregationService.recordSale(event);
    }
}
