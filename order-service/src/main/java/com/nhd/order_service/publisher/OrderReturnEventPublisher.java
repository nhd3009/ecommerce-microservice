package com.nhd.order_service.publisher;

import com.nhd.commonlib.event.order_analytics.OrderReturnCompletedEvent;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.entity.OrderReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReturnEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_RETURNED = "order.returned";
    private static final String TOPIC_ORDER_RETURNED_COMPLETE = "order-return.completed";

    private void publishEvents(OrderReturn entity, OrderItem item) {

        // 1️⃣ Analytics
        OrderReturnCompletedEvent analyticsEvent =
                OrderReturnCompletedEvent.builder()
                        .returnId(entity.getId())
                        .orderId(entity.getOrderId())
                        .orderItemId(entity.getOrderItemId())
                        .productId(item.getProductId())
                        .categoryId(item.getCategoryId())
                        .quantity(entity.getQuantity())
                        .refundRevenue(entity.getRefundAmount())
                        .refundCost(
                                item.getImportPrice()
                                        .multiply(BigDecimal.valueOf(entity.getQuantity()))
                        )
                        .refundProfit(
                                entity.getRefundAmount()
                                        .subtract(
                                                item.getImportPrice()
                                                        .multiply(BigDecimal.valueOf(entity.getQuantity()))
                                        )
                        )
                        .returnDate(
                                entity.getCompletedAt()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                        )
                        .build();

        kafkaTemplate.send(TOPIC_ORDER_RETURNED, analyticsEvent);

        // 2️⃣ Notification
//        OrderReturnCompletedNotificationEvent notifyEvent =
//                OrderReturnCompletedNotificationEvent.builder()
//                        .userId(entity.getUserId())
//                        .orderId(entity.getOrderId())
//                        .returnId(entity.getId())
//                        .refundAmount(entity.getRefundAmount())
//                        .method(entity.getRefundInfo().getMethod())
//                        .message("Your refund has been processed successfully")
//                        .build();

//        kafkaTemplate.send(TOPIC_ORDER_RETURNED_COMPLETE, notifyEvent);

        log.info("[KAFKA] Published return events for returnId={}", entity.getId());
    }
}
