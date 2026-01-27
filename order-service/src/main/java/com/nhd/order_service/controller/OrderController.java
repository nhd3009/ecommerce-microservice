package com.nhd.order_service.controller;

import java.time.LocalDateTime;

import com.nhd.commonlib.response.ApiResponse;
import com.nhd.commonlib.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhd.order_service.dto.OrderDto;
import com.nhd.order_service.enums.OrderStatus;
import com.nhd.order_service.request.CreateOrderRequest;
import com.nhd.order_service.request.UpdateOrderStatusRequest;
import com.nhd.order_service.service.OrderService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
        @RequestBody CreateOrderRequest request,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken) {
        try{
            String token = orderService.getToken(bearerToken, accessToken);
            OrderDto result = orderService.placeOrder(request, token);

            ApiResponse<OrderDto> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Order has been placed successfully!",
                    result
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e){
            throw new RuntimeException("Errors when placing an order: " + e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderId(@RequestHeader(value = "Authorization", required = false) String bearerToken,
                                            @CookieValue(value = "accessToken", required = false) String accessToken,
                                            @PathVariable Long id) {
        try{
            String token = orderService.getToken(bearerToken, accessToken);
            ApiResponse<OrderDto> response = new ApiResponse<>(HttpStatus.OK.value(), "", orderService.getOrderById(id, token));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving an order: " + e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getAllMyOrder(
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        try{
            String token = orderService.getToken(bearerToken, accessToken);
            ApiResponse<PageResponse<OrderDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved all my orders successfully!", orderService.getAllMyOrder(token, page, size));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving all my orders: " + e.getMessage());
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<PageResponse<OrderDto>>> getAllOrdersForAdmin(
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken,
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) LocalDateTime fromDate,
        @RequestParam(required = false) LocalDateTime toDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try{
            String token = orderService.getToken(bearerToken, accessToken);
            ApiResponse<PageResponse<OrderDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved all orders successfully!", orderService.getAllOrdersForAdmin(token, status, userId, fromDate, toDate, page, size));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving all orders: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(@PathVariable("id") Long orderId,
            @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @CookieValue(value = "accessToken", required = false) String accessToken) {
        try{
            String token = orderService.getToken(bearerToken, accessToken);
            ApiResponse<OrderDto> response = new ApiResponse<>(HttpStatus.OK.value(), "", orderService.updateOrderStatus(orderId, request.getStatus(), request.getDeliveryProvider(), request.getTrackingNumber(), token));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Errors when update an order: " + e.getMessage());
        }
    }
    
}
