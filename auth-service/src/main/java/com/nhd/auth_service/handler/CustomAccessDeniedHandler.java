package com.nhd.auth_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhd.commonlib.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
    ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Forbidden", "Access denied");
    apiResponse.setTimestamp(LocalDateTime.now());

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }
}
