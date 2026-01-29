package com.nhd.order_service.service;

import com.nhd.commonlib.dto.UserDto;
import com.nhd.commonlib.exception.BadRequestException;
import com.nhd.commonlib.exception.ResourceNotFoundException;
import com.nhd.commonlib.exception.UnauthorizedException;
import com.nhd.commonlib.response.ApiResponse;
import com.nhd.order_service.client.AuthFeignClient;
import com.nhd.order_service.dto.OrderReturnDto;
import com.nhd.order_service.entity.Order;
import com.nhd.order_service.entity.OrderItem;
import com.nhd.order_service.entity.OrderReturn;
import com.nhd.order_service.enums.OrderStatus;
import com.nhd.order_service.enums.ReturnStatus;
import com.nhd.order_service.mapper.OrderReturnMapper;
import com.nhd.order_service.publisher.OrderReturnApprovedPublisher;
import com.nhd.order_service.repository.OrderItemRepository;
import com.nhd.order_service.repository.OrderRepository;
import com.nhd.order_service.repository.OrderReturnRepository;
import com.nhd.order_service.request.CreateOrderReturnRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderReturnService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final OrderReturnRepository orderReturnRepo;
    private final AuthFeignClient authClient;
    private final OrderReturnApprovedPublisher orderReturnApprovedPublisher;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String ROLE_USER = "ROLE_USER";

    @Transactional
    public OrderReturnDto createReturn(
            Long orderId,
            CreateOrderReturnRequest request,
            String bearerToken
    ) {
        UserDto user = getUserFromToken(bearerToken);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("Not your order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BadRequestException("Your Order is not completed");
        }

        long daysBetween = ChronoUnit.DAYS.between(order.getUpdatedAt(), Instant.now());
        if (daysBetween > 7) {
            throw new BadRequestException("The 7-day return period has expired.");
        }

        OrderItem item = orderItemRepo.findById(request.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        int returnedQty = orderReturnRepo
                .sumReturnedQuantity(orderId, item.getId());

        if (returnedQty + request.getQuantity() > item.getQuantity()) {
            throw new BadRequestException("Return quantity exceeds purchased quantity");
        }
        BigDecimal refundAmount =
                item.getPrice()
                        .multiply(BigDecimal.valueOf(request.getQuantity()));

        OrderReturn entity = OrderReturn.builder()
                .orderId(orderId)
                .orderItemId(item.getId())
                .userId(user.getId())
                .userEmail(!request.getEmail().isEmpty() || request.getEmail() != null ? request.getEmail() : user.getEmail())
                .quantity(request.getQuantity())
                .refundAmount(refundAmount)
                .returnedReason(request.getReason())
                .refundInfo(request.getRefundInfo())
                .status(ReturnStatus.REQUESTED)
                .createdAt(Instant.now())
                .build();

        orderReturnRepo.save(entity);

        return OrderReturnMapper.toDto(entity);
    }

    @Transactional
    public OrderReturnDto approveReturn(Long returnId, String token) {
        UserDto user = getUserFromToken(token);
        if(user.getRoles().contains(ROLE_USER)){
            throw new UnauthorizedException("You are not allowed to set status!");
        }
        OrderReturn entity = orderReturnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found"));

        if (entity.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException("Return not in REQUESTED state");
        }

        OrderItem item = orderItemRepo.findById(entity.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        entity.setStatus(ReturnStatus.APPROVED);
        orderReturnRepo.save(entity);
        orderReturnApprovedPublisher.publish(entity, item);
        return OrderReturnMapper.toDto(entity);
    }

    @Transactional
    public OrderReturnDto rejectReturn(Long returnId, String reason, String token) {
        UserDto user = getUserFromToken(token);
        if(user.getRoles().contains(ROLE_USER)){
            throw new UnauthorizedException("You are not allowed to set status!");
        }
        OrderReturn entity = orderReturnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found"));

        if (entity.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException("Return not in REQUESTED state");
        }

        entity.setStatus(ReturnStatus.REJECTED);
        entity.setRejectedReason(reason);
        orderReturnRepo.save(entity);
        return OrderReturnMapper.toDto(entity);
    }

    public void completeReturn(Long returnId) {
        OrderReturn entity = orderReturnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found"));

        if (entity.getStatus() != ReturnStatus.APPROVED) {
            throw new BadRequestException("Return not approved");
        }

        OrderItem item = orderItemRepo.findById(entity.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        BigDecimal refundAmount =
                item.getPrice()
                        .multiply(BigDecimal.valueOf(entity.getQuantity()));

        entity.setRefundAmount(refundAmount);
        entity.setStatus(ReturnStatus.COMPLETED);
        entity.setCompletedAt(Instant.now());

        orderReturnRepo.save(entity);

        // publishEvents(entity, item);
    }

    public UserDto getUserFromToken(String bearerToken) {
        ApiResponse<UserDto> authResponse = authClient.verifyToken(bearerToken);
        if (authResponse.getData() == null)
            throw new UnauthorizedException("Invalid or missing token");
        return authResponse.getData();
    }

}
