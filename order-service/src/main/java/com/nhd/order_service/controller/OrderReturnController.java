package com.nhd.order_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhd.commonlib.response.ApiResponse;
import com.nhd.order_service.dto.OrderReturnDto;
import com.nhd.order_service.request.CreateOrderReturnRequest;
import com.nhd.order_service.service.OrderReturnService;
import com.nhd.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/v1/order-return/")
@RequiredArgsConstructor
public class OrderReturnController {
    private final OrderReturnService orderReturnService;
    private final OrderService orderService;

    @PostMapping("/user/create/{id}")
    public ResponseEntity<ApiResponse<OrderReturnDto>> createOrderReturn(
        @PathVariable("id") Long orderId,
        @RequestBody CreateOrderReturnRequest request,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        String token = orderService.getToken(bearerToken, accessToken);
        OrderReturnDto dto = orderReturnService.createReturn(orderId, request, token);
        ApiResponse<OrderReturnDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Your order return has been created", dto);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(response);
    }
    
    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<ApiResponse<OrderReturnDto>> approveOrderReturn(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        String token = orderService.getToken(bearerToken, accessToken);
        OrderReturnDto dto = orderReturnService.approveReturn(id, token);
        ApiResponse<OrderReturnDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "The order return has been approved", dto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

    @PutMapping("/admin/{id}/rejected")
    public ResponseEntity<ApiResponse<OrderReturnDto>> rejectedOrderReturn(
        @PathVariable Long id,
        @RequestBody String reason,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        String token = orderService.getToken(bearerToken, accessToken);
        OrderReturnDto dto = orderReturnService.rejectReturn(id, reason, token);
        ApiResponse<OrderReturnDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "The order return has been rejected", dto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

    @PutMapping("/admin/{id}/complete")
    public ResponseEntity<ApiResponse<OrderReturnDto>> completeOrderReturn(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String bearerToken,
        @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        String token = orderService.getToken(bearerToken, accessToken);
        OrderReturnDto dto = orderReturnService.completeReturn(id, token);
        ApiResponse<OrderReturnDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "The order return has been completed", dto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }
}
