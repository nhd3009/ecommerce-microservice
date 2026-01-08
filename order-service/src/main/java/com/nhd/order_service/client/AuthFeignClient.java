package com.nhd.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nhd.order_service.dto.UserDto;
import com.nhd.order_service.response.ApiResponse;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthFeignClient {

    @GetMapping("/api/v1/auth/verify")
    ApiResponse<UserDto> verifyToken(
        @RequestHeader(value = "Authorization", required = false) String bearerToken
    );
}
