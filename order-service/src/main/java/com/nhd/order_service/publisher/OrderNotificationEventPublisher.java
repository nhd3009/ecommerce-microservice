package com.nhd.order_service.publisher;

import com.nhd.commonlib.event.order_notification.OrderNotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-notification-events";

    public void publishOrderNotification(OrderNotificationEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event);
            log.info("[KAFKA PUBLISH] Sent OrderNotificationEvent for orderId {}", event.getOrderId());
        } catch (Exception e) {
            log.error("[KAFKA ERROR] Failed to send event for orderId {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}
