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
public class OrderReturnCompletedPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_RETURNED = "order.returned.complete";

    private void publishEvents(OrderReturn entity, OrderItem item) {

        OrderReturnCompletedEvent analyticsEvent =
                OrderReturnCompletedEvent.builder()
                        .returnId(entity.getId())
                        .orderId(entity.getOrderId())
                        .orderItemId(entity.getOrderItemId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .categoryId(item.getCategoryId())
                        .categoryName(item.getCategoryName())
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

        log.info("[KAFKA] Published return events for returnId={}", entity.getId());
    }
}
