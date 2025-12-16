package com.nhd.auth_service.controller;

import com.nhd.auth_service.dto.AuthResponse;
import com.nhd.auth_service.dto.LoginRequest;
import com.nhd.auth_service.dto.RefreshTokenRequest;
import com.nhd.auth_service.dto.RegisterRequest;
import com.nhd.auth_service.dto.UserDto;
import com.nhd.auth_service.exception.AuthException;
import com.nhd.auth_service.response.ApiResponse;
import com.nhd.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    ApiResponse<?> response = authService.register(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    ApiResponse<?> response = authService.login(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
    ApiResponse<AuthResponse> response = authService.refreshToken(request.getRefreshToken());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    System.out.println("Logout user: " + username);
    ApiResponse<String> response = authService.logout(username);
    return ResponseEntity.ok(response);
  }

}
