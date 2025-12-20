package com.nhd.auth_service.service;

import com.nhd.auth_service.dto.AuthResponse;
import com.nhd.auth_service.dto.LoginRequest;
import com.nhd.auth_service.dto.RegisterRequest;
import com.nhd.auth_service.dto.UserDto;
import com.nhd.auth_service.entity.Role;
import com.nhd.auth_service.entity.User;
import com.nhd.auth_service.exception.AuthException;
import com.nhd.auth_service.mapper.UserMapper;
import com.nhd.auth_service.repository.RoleRepository;
import com.nhd.auth_service.repository.UserRepository;
import com.nhd.auth_service.response.ApiResponse;

import io.jsonwebtoken.Claims;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public ApiResponse<UserDto> register(RegisterRequest request) {
    try{
      if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        throw new RuntimeException("Username already exists");
      }
      if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("Email already exists");
      }

      Role roleUser = roleRepository.findByName("ROLE_USER")
          .orElseThrow(() -> new RuntimeException("Role ROLE_USER not found"));

      User user = new User();
      user.setUsername(request.getUsername());
      user.setEmail(request.getEmail());
      user.setPassword(passwordEncoder.encode(request.getPassword()));
      user.getRoles().add(roleUser);

      userRepository.save(user);
      UserDto userDto = UserMapper.toDto(user);
      return new ApiResponse<>(userDto, HttpStatus.CREATED.value(), "User has been created successfully!");
    } catch (Exception e){
      throw new RuntimeException("Error registering user: " + e.getMessage());
    }
  }

  public ApiResponse<AuthResponse> login(LoginRequest request) {
    try {
      User user = userRepository.findByUsername(request.getUsername())
          .orElseThrow(() -> new AuthException("User doesn't existed", HttpStatus.UNAUTHORIZED));

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new AuthException("Invalid username or password", HttpStatus.UNAUTHORIZED);
      }
      AuthResponse response = createdRefreshToken(user);

      return new ApiResponse<>(response, HttpStatus.OK.value(), "Login successful!");

    } catch (AuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error logging in: " + e.getMessage());
    }
  }

  public ApiResponse<AuthResponse> refreshToken(String refreshToken) {
    try {
      User user = userRepository.findByRefreshToken(refreshToken)
          .orElseThrow(() -> new AuthException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

      if (user.getRefreshTokenExpired() == null || user.getRefreshTokenExpired().isBefore(Instant.now())) {
        throw new AuthException("Refresh token expired", HttpStatus.UNAUTHORIZED);
      }

      AuthResponse response = createdRefreshToken(user);

      return new ApiResponse<>(response, HttpStatus.OK.value(), "Refresh Token successful!");
    } catch (AuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error refreshing token: " + e.getMessage());
    }

  }

  public ApiResponse<?> verifyToken(String token) {
    if (token == null || token.isEmpty()) {
        return new ApiResponse<>(
                "Missing or invalid access token",
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized"
        );
    }

    try {
        Claims claims = jwtService.extractAllClaims(token);

        UserDto userInfo = new UserDto();
        userInfo.setId(Long.valueOf(claims.getSubject())); // subject = userId
        userInfo.setUsername(claims.get("username", String.class));
        userInfo.setEmail(claims.get("email", String.class));
        userInfo.setRoles(claims.get("roles", List.class));

        return new ApiResponse<>(
                userInfo,
                HttpStatus.OK.value(),
                "Token verified"
        );

    } catch (Exception e) {
        return new ApiResponse<>(
                "Invalid or expired token",
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized"
        );
    }
  }

  public ApiResponse<String> logout(String username) {
    try{
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new AuthException("User not found", HttpStatus.UNAUTHORIZED));

      user.setRefreshToken(null);
      user.setRefreshTokenExpired(null);
      userRepository.save(user);

      return new ApiResponse<>("Logout successful!", HttpStatus.OK.value(), "Logout successful!");
    } catch (AuthException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error when logout: " + e.getMessage());
    }

  }


  public AuthResponse createdRefreshToken(User user){
    String newAccessToken = jwtService.generateToken(user);
    String newRefreshToken = UUID.randomUUID().toString();
    Instant newRefreshExpired = Instant.now().plus(7, ChronoUnit.DAYS);

    user.setRefreshToken(newRefreshToken);
    user.setRefreshTokenExpired(newRefreshExpired);
    userRepository.save(user);

    AuthResponse response = AuthResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .user(UserMapper.toDto(user))
        .build();
    return response;
  }
}
