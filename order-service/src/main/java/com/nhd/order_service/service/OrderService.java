package com.nhd.order_service.service;

import org.springframework.stereotype.Service;

import com.nhd.order_service.repository.OrderItemRepository;
import com.nhd.order_service.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
}
