package com.nhd.order_service.publisher;

import com.nhd.commonlib.event.order_analytics.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAnalyticsEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(OrderCompletedEvent event) {
        try {
            kafkaTemplate.send(
                    "order.completed",
                    event.getOrderId().toString(),
                    event
            );

            log.info(
                    "[KAFKA] Published OrderCompletedEvent orderId={}",
                    event.getOrderId()
            );
        } catch (Exception e) {
            log.error(
                    "[KAFKA ERROR] Failed to publish OrderCompletedEvent orderId={}",
                    event.getOrderId(),
                    e
            );
            throw e;
        }
    }
}
