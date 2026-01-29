package com.nhd.notification_service.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nhd.commonlib.event.order_notification.OrderReturnApprovedEvent;
import com.nhd.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderRerturnApprovedListener {

    private final EmailService emailService;

    @KafkaListener(
        topics = "order.returned.approved",
        groupId = "notification-service-group",
        containerFactory = "orderReturnApprovedKafkaListenerContainerFactory"
    )
    public void handleNotification(OrderReturnApprovedEvent event) {
        log.info("[NOTIFICATION] Received event: {}", event);
        try {
            
            emailService.sendOrderReturnApprovedEmail(event);
            log.info("[EMAIL SENT] To userId={}, email={}", event.getUserId(), event.getEmail());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
            throw e;
        }
    }
}
