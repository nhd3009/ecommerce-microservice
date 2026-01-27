package com.nhd.auth_service.controller;

import com.nhd.auth_service.dto.AuthResponse;
import com.nhd.auth_service.dto.LoginRequest;
import com.nhd.auth_service.dto.RegisterRequest;
import com.nhd.auth_service.dto.UserDto;
import com.nhd.auth_service.exception.AuthException;
import com.nhd.auth_service.service.AuthService;
import com.nhd.auth_service.service.JwtService;

import com.nhd.commonlib.response.ApiResponse;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
    try{
      UserDto user = authService.register(request);
      ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "User is created successfully!", user);
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(response);
    } catch (Exception e) {
      throw new RuntimeException("Error registering user: " + e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try{
      AuthResponse auth = authService.login(request);
      ApiResponse<AuthResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Login successfully", auth);
      return setCookiesWithToken(response);
    } catch (AuthException e) {
      throw new AuthException("Error logging in: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      throw new RuntimeException("Error logging in: " + e.getMessage());
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken) {
    try {
      AuthResponse auth = authService.refreshToken(refreshToken);
      ApiResponse<AuthResponse> response = new ApiResponse<>(HttpStatus.OK.value(),  "Refresh Token successful!", auth);
      return setCookiesWithToken(response);
    } catch (AuthException e) {
      throw new AuthException("Error refreshing token in: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      throw new RuntimeException("Error refreshing token in: " + e.getMessage());
    }
  }

  @GetMapping("/verify")
  public ResponseEntity<ApiResponse<UserDto>> verifyToken(
      @CookieValue(value = "accessToken", required = false) String accessToken,
      @RequestHeader(value = "Authorization", required = false) String authHeader
  ) {
    try {
      String token = null;
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
      } else if (accessToken != null && !accessToken.isEmpty()) {
        token = accessToken;
      }
      UserDto userInfo = authService.verifyToken(token);
      ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Token verified", userInfo);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new RuntimeException("Invalid or expired token: " + e.getMessage());
    }

  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(
      @CookieValue(value = "accessToken", required = false) String accessToken,
      @RequestHeader(value = "Authorization", required = false) String authHeader
  ) {
    try{
      String token = null;
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
      } else if (accessToken != null && !accessToken.isEmpty()) {
        token = accessToken;
      }
      String username = jwtService.extractUsername(token);
      String msg = authService.logout(username);
      ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), msg, null);

      ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(0)
              .build();

      ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(0)
              .build();

      return ResponseEntity.status(HttpStatus.OK.value())
              .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
              .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
              .body(response);
    } catch (AuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error when logout: " + e.getMessage());
    }
  }

  @NonNull
  public ResponseEntity<?> setCookiesWithToken(ApiResponse<AuthResponse> response) {
    String accessToken = response.getData().getAccessToken();
    String refreshToken = response.getData().getRefreshToken();
    int status = response.getStatusCode();

    ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofHours(2))
            .sameSite("Lax")
            .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofDays(7))
            .sameSite("Lax")
            .build();

    return ResponseEntity.status(status)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
  }

}
