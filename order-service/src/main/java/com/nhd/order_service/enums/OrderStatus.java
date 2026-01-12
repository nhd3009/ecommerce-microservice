package com.nhd.order_service.enums;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    COMPLETED,
    CANCELLED;

    public static boolean canTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == PROCESSING || to == CANCELLED;
            case PROCESSING -> to == SHIPPED || to == CANCELLED;
            case SHIPPED -> to == COMPLETED;
            default -> false;
        };
    }
}
