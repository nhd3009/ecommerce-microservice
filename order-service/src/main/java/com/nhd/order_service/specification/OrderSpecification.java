package com.nhd.order_service.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.nhd.order_service.entity.Order;
import com.nhd.order_service.enums.OrderStatus;

public class OrderSpecification {
    public static Specification<Order> hasStatus(OrderStatus status) {
            return (root, query, cb) ->
                    status == null ? null : cb.equal(root.get("status"), status);
        }

        public static Specification<Order> hasUserId(Long userId) {
            return (root, query, cb) ->
                    userId == null ? null : cb.equal(root.get("userId"), userId);
        }

        public static Specification<Order> createdAfter(LocalDateTime fromDate) {
            return (root, query, cb) ->
                    fromDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
        }

        public static Specification<Order> createdBefore(LocalDateTime toDate) {
            return (root, query, cb) ->
                    toDate == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
        }
}
