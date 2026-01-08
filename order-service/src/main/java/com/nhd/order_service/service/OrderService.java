package com.nhd.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nhd.order_service.client.AuthFeignClient;
import com.nhd.order_service.client.ProductFeignClient;
import com.nhd.order_service.dto.OrderDto;
import com.nhd.order_service.dto.OrderItemDto;
import com.nhd.order_service.dto.ProductDto;
import com.nhd.order_service.dto.UserDto;
import com.nhd.order_service.entity.Order;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.enums.OrderStatus;
import com.nhd.order_service.exception.BadRequestException;
import com.nhd.order_service.exception.ResourceNotFoundException;
import com.nhd.order_service.exception.UnauthorizedException;
import com.nhd.order_service.repository.OrderItemRepository;
import com.nhd.order_service.repository.OrderRepository;
import com.nhd.order_service.request.CreateOrderRequest;
import com.nhd.order_service.request.OrderItemRequest;
import com.nhd.order_service.response.ApiResponse;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthFeignClient authClient;
    private final ProductFeignClient productClient;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, AuthFeignClient authClient, ProductFeignClient productClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authClient = authClient;
        this.productClient = productClient;
    }
    
    public ApiResponse<OrderDto> placeOrder(CreateOrderRequest request, String bearerToken) { 

        ApiResponse<UserDto> authResponse = authClient.verifyToken(bearerToken); 
        if (authResponse.getData() == null) 
            throw new UnauthorizedException("Invalid or missing token"); UserDto user = authResponse.getData(); 
        
        List<OrderItem> orderItems = new ArrayList<>(); 
        BigDecimal totalPrice = BigDecimal.ZERO; 
        for (OrderItemRequest itemReq : request.getItems()) { 
            ResponseEntity<ApiResponse<ProductDto>> productResponse = productClient.getProductById(itemReq.getProductId()); 
            ProductDto product = productResponse.getBody().getData(); 
            if (product == null) { 
                throw new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()); 
            } 
            if (product.getStockQuantity() < itemReq.getQuantity()) { 
                throw new BadRequestException("Not enough stock for product: " + product.getName()); 
            } 


            productClient.decreaseProductStock(product.getId(), itemReq.getQuantity());
            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())); 
            totalPrice = totalPrice.add(subTotal); 


            OrderItem orderItem = OrderItem.builder().productId(product.getId()).productName(product.getName()).price(product.getPrice()).quantity(itemReq.getQuantity()).subTotal(subTotal).build(); 
            orderItems.add(orderItem); 
        } 


        Order order = Order.builder().userId(user.getId()).totalAmount(totalPrice).status(OrderStatus.PENDING).build(); 
        orderItems.forEach(item -> item.setOrder(order)); 
        order.setItems(orderItems); 
        Order savedOrder = orderRepository.save(order); 

        
        List<OrderItemDto> itemDtos = savedOrder.getItems()
                                            .stream()
                                            .map(i -> new OrderItemDto(i.getProductId(), i.getProductName(), i.getPrice(), i.getQuantity(), i.getSubTotal()))
                                            .toList(); 
        OrderDto dto = OrderDto.builder()
                                    .id(savedOrder.getId())
                                    .userId(savedOrder.getUserId())
                                    .totalAmount(savedOrder.getTotalAmount())
                                    .status(savedOrder.getStatus())
                                    .createdAt(savedOrder.getCreatedAt())
                                    .items(itemDtos)
                                    .build(); 
        return new ApiResponse<>(dto, HttpStatus.CREATED.value(), "Order placed successfully"); }
}
