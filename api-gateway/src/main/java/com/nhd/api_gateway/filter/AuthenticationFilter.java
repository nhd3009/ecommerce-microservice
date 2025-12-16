package com.nhd.api_gateway.filter;

import com.nhd.api_gateway.util.JwtUtil;
import com.nhd.api_gateway.util.ResponseUtil;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
  private final JwtUtil jwtUtil;

  public AuthenticationFilter(JwtUtil jwtUtil) {
    super(Config.class);
    this.jwtUtil = jwtUtil;
  }

  public static class Config {}

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseUtil.writeError(
            exchange,
            HttpStatus.UNAUTHORIZED,
            "Unauthorized",
            "Missing or invalid Authorization header"
        );
      }

      String token = authHeader.substring(7);

      try {
        jwtUtil.validateToken(token);

        String username = jwtUtil.extractUsername(token);
        List<String> roles = jwtUtil.extractRoles(token);

        ServerHttpRequest request = exchange.getRequest().mutate()
            .header("X-Authenticated-User", username)
            .header("X-Authenticated-Roles", String.join(",", roles))
            .build();

        return chain.filter(exchange.mutate().request(request).build());

      } catch (Exception e) {
        return ResponseUtil.writeError(
            exchange,
            HttpStatus.UNAUTHORIZED,
            "Unauthorized",
            e.getMessage()
        );
      }
    };
  }

}
