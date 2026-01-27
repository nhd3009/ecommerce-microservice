package com.nhd.auth_service.controller;

import com.nhd.auth_service.dto.AuthResponse;
import com.nhd.auth_service.dto.LoginRequest;
import com.nhd.auth_service.dto.RegisterRequest;
import com.nhd.auth_service.dto.UserDto;
import com.nhd.auth_service.service.AuthService;
import com.nhd.auth_service.service.JwtService;

import com.nhd.commonlib.response.ApiResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.Set;

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
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    ApiResponse<?> response = authService.register(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    ApiResponse<AuthResponse> response = authService.login(request);
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

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken) {
    ApiResponse<AuthResponse> response = authService.refreshToken(refreshToken);
    String newAccessToken = response.getData().getAccessToken();
    String newRefreshToken = response.getData().getRefreshToken();
    int status = response.getStatusCode();

    ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofHours(2))
            .sameSite("Lax")
            .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
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

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    String username = jwtService.extractUsername(token);
    ApiResponse<String> response = authService.logout(username);
    int status = response.getStatusCode();

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

    return ResponseEntity.status(status)
            .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
            .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
            .body(response);
  }

  @GetMapping("/verify")
    public ResponseEntity<ApiResponse<UserDto>> verifyToken(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (accessToken != null && !accessToken.isEmpty()) {
            token = accessToken;
        }

        ApiResponse<UserDto> response = authService.verifyToken(token);
        HttpStatus status = response.getStatusCode() == 200 ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(response);
    }
}
