package com.nhd.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhd.order_service.dto.ProductDto;
import com.nhd.order_service.response.ApiResponse;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductFeignClient {
    @GetMapping("/api/v1/products/{id}")
    ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id);

    @PostMapping("/api/v1/products/{id}/adjust-stock")
    ResponseEntity<ApiResponse<String>> adjustProductStock(@PathVariable Long id, @RequestParam int quantity);
}
