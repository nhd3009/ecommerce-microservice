package com.nhd.api_gateway.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
  private LocalDateTime timestamp;
  private int statusCode;
  private String message;
  private T data;

  public ApiResponse(int statusCode, String message, T data) {
    this.timestamp = LocalDateTime.now();
    this.statusCode = statusCode;
    this.message = message;
    this.data = data;
  }
}
