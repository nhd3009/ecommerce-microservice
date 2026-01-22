package com.nhd.notification_service.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nhd.notification_service.dto.OrderNotificationEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationListener {
    @KafkaListener(
        topics = "order-notification-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(OrderNotificationEvent event) {
        log.info("[NOTIFICATION] Received event: {}", event);

        try {
            log.info("[EMAIL SENT] To userId={}, message={}", event.getUserId(), event.getMessage());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
            throw e;
        }
    }
}
