package com.nhd.auth_service.handler;

import com.nhd.auth_service.exception.AuthException;
import com.nhd.commonlib.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ApiResponse<String>> handleAuthException(AuthException ex) {
    ApiResponse<String> response = new ApiResponse<>(ex.getStatus().value(), "Unauthorized", ex.getMessage()


    );
    return ResponseEntity.status(ex.getStatus()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
    ApiResponse<String> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error", ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
