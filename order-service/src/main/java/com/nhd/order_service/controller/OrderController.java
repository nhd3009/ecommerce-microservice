package com.nhd.order_service.controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhd.order_service.dto.OrderDto;
import com.nhd.order_service.exception.UnauthorizedException;
import com.nhd.order_service.request.CreateOrderRequest;
import com.nhd.order_service.request.UpdateOrderStatusRequest;
import com.nhd.order_service.response.ApiResponse;
import com.nhd.order_service.response.PageResponse;
import com.nhd.order_service.service.OrderService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ApiResponse<OrderDto> placeOrder(
        @RequestBody CreateOrderRequest request,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken) {

        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken;
        } else if (accessToken != null) {
            token = "Bearer " + accessToken;
        }

        if (token == null) {
            throw new UnauthorizedException("Missing token");
        }

        return orderService.placeOrder(request, token);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDto> getOrderId(@RequestHeader(value = "Authorization", required = false) String bearerToken,
                                            @CookieValue(value = "accessToken", required = false) String accessToken,
                                            @PathVariable("id") Long id) {
        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken;
        } else if (accessToken != null) {
            token = "Bearer " + accessToken;
        }

        if (token == null) {
            throw new UnauthorizedException("Missing token");
        }
        return orderService.getOrderById(id, token);
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderDto>> getAllOrders(
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken;
        } else if (accessToken != null) {
            token = "Bearer " + accessToken;
        }

        if (token == null) {
            throw new UnauthorizedException("Missing token");
        }
        return orderService.getAllOrderFromUser(token, page, size);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<OrderDto> updateOrderStatus(@PathVariable("id") Long orderId,
            @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(value = "accessToken", required = false) String accessToken) {
        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken;
        } else if (accessToken != null) {
            token = "Bearer " + accessToken;
        }

        if (token == null) {
            throw new UnauthorizedException("Missing token");
        }
        return orderService.updateOrderStatus(orderId, request.getStatus(), request.getDeliveryProvider(), request.getTrackingNumber(), token);
    }
    
}
