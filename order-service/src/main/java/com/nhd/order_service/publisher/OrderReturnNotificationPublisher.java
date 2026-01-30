package com.nhd.order_service.publisher;

import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nhd.commonlib.event.order_notification.OrderReturnNotificationEvent;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.entity.OrderReturn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReturnNotificationPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_RETURN_APPROVED = "order.returned.notification";

    public void publish(OrderReturn orderReturn, OrderItem orderItem) {

        OrderReturnNotificationEvent event = OrderReturnNotificationEvent.builder()
                .returnId(orderReturn.getId())
                .orderId(orderReturn.getOrderId())
                .userId(orderReturn.getUserId())
                .email(orderReturn.getUserEmail())
                .productName(orderItem.getProductName())
                .quantity(orderReturn.getQuantity())
                .refundAmount(orderReturn.getRefundAmount())
                .refundMethod(orderReturn.getRefundInfo().getMethod().toString())
                .receiver(orderReturn.getRefundInfo().getReceiver().toString())
                .note(orderReturn.getRefundInfo().getNote().toString())
                .reason(orderReturn.getReturnedReason())
                .rejectedReason(orderReturn.getRejectedReason())
                .approvedAt(Instant.now())
                .build();

        kafkaTemplate.send(
                TOPIC_ORDER_RETURN_APPROVED,
                orderReturn.getOrderId().toString(),
                event
        );

        log.info("Published OrderReturnApprovedEvent for returnId={}", orderReturn.getId());
    }
}
