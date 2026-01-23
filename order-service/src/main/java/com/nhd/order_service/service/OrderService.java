package com.nhd.order_service.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhd.order_service.client.AuthFeignClient;
import com.nhd.order_service.client.ProductFeignClient;
import com.nhd.order_service.config.OrderEventPublisher;
import com.nhd.order_service.dto.OrderDto;
import com.nhd.order_service.dto.OrderItemEvent;
import com.nhd.order_service.dto.OrderNotificationEvent;
import com.nhd.order_service.dto.ProductDto;
import com.nhd.order_service.dto.UserDto;
import com.nhd.order_service.entity.Order;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.enums.OrderStatus;
import com.nhd.order_service.exception.BadRequestException;
import com.nhd.order_service.exception.ResourceNotFoundException;
import com.nhd.order_service.exception.UnauthorizedException;
import com.nhd.order_service.mapper.OrderMapper;
import com.nhd.order_service.mapper.PageResponseMapper;
import com.nhd.order_service.repository.OrderRepository;
import com.nhd.order_service.request.CreateOrderRequest;
import com.nhd.order_service.request.OrderItemRequest;
import com.nhd.order_service.response.ApiResponse;
import com.nhd.order_service.response.PageResponse;
import com.nhd.order_service.specification.OrderSpecification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthFeignClient authClient;
    private final ProductFeignClient productClient;
    private final OrderEventPublisher orderEventPublisher;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_USER = "ROLE_USER";

    public OrderService(OrderRepository orderRepository, AuthFeignClient authClient, ProductFeignClient productClient, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.authClient = authClient;
        this.productClient = productClient;
        this.orderEventPublisher = orderEventPublisher;
    }
    
    @Transactional
    public ApiResponse<OrderDto> placeOrder(CreateOrderRequest request, String bearerToken) { 
        try{
            UserDto user = getUserFromToken(bearerToken); 
        
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

                productClient.adjustProductStock(product.getId(), itemReq.getQuantity());
                BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())); 
                totalPrice = totalPrice.add(subTotal); 

                OrderItem orderItem = OrderItem.builder().productId(product.getId()).productName(product.getName()).price(product.getPrice()).quantity(itemReq.getQuantity()).subTotal(subTotal).build(); 
                orderItems.add(orderItem); 
            } 

            Order order = Order.builder()
                                .userId(user.getId())
                                .orderEmail(request.getOrderEmail().isEmpty() || request.getOrderEmail() == null ? user.getEmail() : request.getOrderEmail())
                                .totalAmount(totalPrice)
                                .status(OrderStatus.PENDING)
                                .note(request.getNote())
                                .recipientPhone(request.getRecipientPhone())
                                .shippingAddress(request.getShippingAddress())
                                .recipientName(request.getRecipientName())
                                .build(); 
            orderItems.forEach(item -> item.setOrder(order)); 
            order.setItems(orderItems); 
            Order savedOrder = orderRepository.save(order); 

            OrderNotificationEvent orderEvent = returnNotificationEvent(savedOrder);
            orderEventPublisher.publishOrderNotification(orderEvent);
            return new ApiResponse<>(OrderMapper.toOrderDto(savedOrder), HttpStatus.CREATED.value(), "Order placed successfully");
        } catch (Exception e) {
            log.error("[ORDER ERROR] {}", e.getMessage(), e);
            throw e;
        }
        
    }

    public ApiResponse<OrderDto> getOrderById(Long orderId, String bearerToken) {
        UserDto user = getUserFromToken(bearerToken);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isAdminOrEmployee = user.getRoles().contains(ROLE_ADMIN) || user.getRoles().contains(ROLE_EMPLOYEE);

        if (!isAdminOrEmployee && !order.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return new ApiResponse<>(OrderMapper.toOrderDto(order), HttpStatus.OK.value(), "Order retrieved successfully");
    }

    public ApiResponse<PageResponse<OrderDto>> getAllMyOrder(String bearerToken, int page, int size) {
        UserDto user = getUserFromToken(bearerToken);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Order> ordersPage = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        Page<OrderDto> dtoPage = ordersPage.map(OrderMapper::toOrderDto);

        return new ApiResponse<>(PageResponseMapper.fromPage(dtoPage), HttpStatus.OK.value(), "Orders retrieved successfully");
    }

    public ApiResponse<PageResponse<OrderDto>> getAllOrdersForAdmin(String bearerToken, OrderStatus status, Long userId, LocalDateTime fromDate, LocalDateTime toDate, int page, int size) {
        UserDto user = getUserFromToken(bearerToken);
        boolean isAdminOrEmployee = user.getRoles().contains(ROLE_ADMIN) || user.getRoles().contains(ROLE_EMPLOYEE);
        if (!isAdminOrEmployee)
            throw new UnauthorizedException("Access denied: Admin or Employee only");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Specification<Order> spec = Specification.allOf(
                OrderSpecification.hasStatus(status),
                OrderSpecification.hasUserId(userId),
                OrderSpecification.createdAfter(fromDate),
                OrderSpecification.createdBefore(toDate)
        );
        Page<OrderDto> dtoPage = orderRepository.findAll(spec, pageable).map(OrderMapper::toOrderDto);
        return new ApiResponse<>(PageResponseMapper.fromPage(dtoPage), HttpStatus.OK.value(), "Orders retrieved successfully");
    }

    public ApiResponse<OrderDto> updateOrderStatus(Long orderId, OrderStatus newStatus, String deliveryProvider, String trackingNumber, String bearerToken){
        UserDto user = getUserFromToken(bearerToken);

        boolean isAdmin = user.getRoles().contains(ROLE_ADMIN);
        boolean isEmployee = user.getRoles().contains(ROLE_EMPLOYEE);
        boolean isUser = user.getRoles().contains(ROLE_USER);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (newStatus == null) throw new BadRequestException("Order status must not be null");

        // Cancel order cannot be modified
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cancelled orders cannot be modified. Please create a new order instead.");
        }

        // For user role
        if (isUser) {
            if (!order.getUserId().equals(user.getId())) {
                throw new UnauthorizedException("You cannot update someone else's order");
            }
            if (newStatus != OrderStatus.CANCELLED) {
                throw new UnauthorizedException("You can only cancel your own order");
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new BadRequestException("You can only cancel orders that are still pending");
            }
        }

        // For employee role
        if (isEmployee) {
            if (newStatus == OrderStatus.CANCELLED) {
                throw new UnauthorizedException("Employees cannot cancel orders");
            }
        }

        // Shipped order rule
        if (newStatus == OrderStatus.DELIVERING) {
            if (deliveryProvider == null || deliveryProvider.isEmpty() || trackingNumber == null || trackingNumber.isEmpty()) {
                throw new BadRequestException("Must provide deliveryProvider and trackingNumber when marking as SHIPPED");
            }
            if (order.getStatus() != OrderStatus.PROCESSING) {
                throw new BadRequestException("Order must be in PROCESSING before being marked as SHIPPED");
            }

            order.setDeliveryProvider(deliveryProvider);
            order.setTrackingNumber(trackingNumber);
        }

        // Order status rule
        if (!OrderStatus.canTransition(order.getStatus(), newStatus)) {
            throw new BadRequestException("Invalid order status transition");
        }

        // Return stock when order is cancelled
        if (newStatus == OrderStatus.CANCELLED && (isUser || isAdmin)) {
            if (order.getStatus() == OrderStatus.DELIVERING || order.getStatus() == OrderStatus.COMPLETED){
                throw new BadRequestException("Cannot cancel a shipped or completed order");
            }
            if (order.getStatus() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    productClient.adjustProductStock(item.getProductId(), -item.getQuantity());
                }
            }
        }
        order.setStatus(newStatus);
        orderRepository.save(order);

        if (List.of(OrderStatus.DELIVERING, OrderStatus.CANCELLED).contains(newStatus)) {

            OrderNotificationEvent event = returnNotificationEvent(order);
            if(newStatus.equals(OrderStatus.DELIVERING)){
                event.setMessage("Your order #" + order.getId() + " has been shipped via "
                                                + deliveryProvider + ", tracking number " + trackingNumber + ".");
            }
            if(newStatus.equals(OrderStatus.CANCELLED)){
                event.setMessage("Your order #" + order.getId() + " has been cancelled.");
            }
            orderEventPublisher.publishOrderNotification(event);
        }
        return new ApiResponse<>(OrderMapper.toOrderDto(order), HttpStatus.OK.value(), "Order status updated successfully");
    }

    public UserDto getUserFromToken(String bearerToken) {
        ApiResponse<UserDto> authResponse = authClient.verifyToken(bearerToken);
        if (authResponse.getData() == null)
            throw new UnauthorizedException("Invalid or missing token");
        return authResponse.getData();
    }

    public String getToken(String bearerToken, String accessToken){
        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken;
        } else if (accessToken != null) {
            token = "Bearer " + accessToken;
        }

        if (token == null) {
            throw new UnauthorizedException("Missing token");
        }
        return token;
    }

    public OrderNotificationEvent returnNotificationEvent(Order savedOrder){
        List<OrderItemEvent> orderItemEvents = savedOrder.getItems().stream()
                                            .map(i -> OrderItemEvent.builder()
                                                .productId(i.getProductId())
                                                .price(i.getPrice())
                                                .productName(i.getProductName())
                                                .quantity(i.getQuantity())
                                                .subTotal(i.getSubTotal())
                                                .build())
                                                .toList();
        OrderNotificationEvent orderEvent = OrderNotificationEvent.builder()
                                            .orderId(savedOrder.getId())
                                            .userId(savedOrder.getUserId())
                                            .email(savedOrder.getOrderEmail())
                                            .message("Your order #" + savedOrder.getId() + " has been placed successfully!")
                                            .status(savedOrder.getStatus().name())
                                            .timestamp(Instant.now())
                                            .items(orderItemEvents)
                                            .totalAmount(savedOrder.getTotalAmount())
                                            .build();
        return orderEvent;
    }
}
