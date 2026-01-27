package com.nhd.notification_service.listener;

import com.nhd.commonlib.event.order_notification.OrderNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nhd.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final EmailService emailService;

    @KafkaListener(
        topics = "order-notification-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(OrderNotificationEvent event) {
        log.info("[NOTIFICATION] Received event: {}", event);
        try {
            
            emailService.sendOrderEmail(event);
            log.info("[EMAIL SENT] To userId={}, message={}", event.getUserId(), event.getMessage());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
            throw e;
        }
    }
}
