package com.nhd.order_service.client;

import com.nhd.commonlib.dto.UserDto;
import com.nhd.commonlib.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthFeignClient {

    @GetMapping("/api/v1/auth/verify")
    ApiResponse<UserDto> verifyToken(
        @RequestHeader(value = "Authorization", required = false) String bearerToken
    );
}
