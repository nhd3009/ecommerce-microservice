package com.nhd.auth_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
  private HttpStatus status;

  public AuthException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

}
