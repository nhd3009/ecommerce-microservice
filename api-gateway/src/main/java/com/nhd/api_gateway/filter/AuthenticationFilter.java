package com.nhd.api_gateway.filter;

import com.nhd.api_gateway.util.JwtUtil;
import com.nhd.api_gateway.util.ResponseUtil;

import io.jsonwebtoken.Claims;

import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
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
            String token = null;

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null) {
                HttpCookie cookie = exchange.getRequest().getCookies().getFirst("accessToken");
                if (cookie != null) {
                    token = cookie.getValue();
                }
            }

            if (token == null || token.isEmpty()) {
                return ResponseUtil.writeError(exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        "Missing access token");
            }

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                String userId = claims.getSubject();
                String email = claims.get("email", String.class);
                List<String> rolesList = claims.get("roles", List.class);
                String roles = String.join(",", rolesList);

                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-UserId", userId)
                        .header("X-Authenticated-Email", email)
                        .header("X-Authenticated-Roles", roles)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return ResponseUtil.writeError(exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        "Invalid or expired token");
            }
        };
    }

}
