package com.nhd.order_service.publisher;

import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nhd.commonlib.event.order_notification.OrderReturnApprovedEvent;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.entity.OrderReturn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReturnApprovedPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_RETURN_APPROVED = "order.returned.approved";

    public void publish(OrderReturn orderReturn, OrderItem orderItem) {

        OrderReturnApprovedEvent event = OrderReturnApprovedEvent.builder()
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
