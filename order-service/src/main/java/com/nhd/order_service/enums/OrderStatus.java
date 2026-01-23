package com.nhd.order_service.enums;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    DELIVERING,
    COMPLETED,
    CANCELLED;

    public static boolean canTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == PROCESSING || to == CANCELLED;
            case PROCESSING -> to == DELIVERING || to == CANCELLED;
            case DELIVERING -> to == COMPLETED;
            default -> false;
        };
    }
}
