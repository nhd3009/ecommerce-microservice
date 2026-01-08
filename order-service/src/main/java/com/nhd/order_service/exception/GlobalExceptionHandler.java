package com.nhd.order_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhd.order_service.response.ApiResponse;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(), HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicate(DuplicateException ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                "DUPLICATE_RESOURCE"
                );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(BadRequestException ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST"
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = new ApiResponse<>(errors,
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed"
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedException(UnauthorizedException ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED"
                );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        int status = ex.status() != 0 ? ex.status() : 500;
        String message = "Upstream service error";

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(ex.contentUTF8());
            if (node.has("message")) {
                message = node.get("message").asText();
            }
        } catch (Exception ignored) {}

        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(message, status, "Feign Error"));
    }
}
