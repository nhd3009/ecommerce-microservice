package com.nhd.api_gateway.filter;

import com.nhd.api_gateway.filter.RoleFilter.Config;
import com.nhd.api_gateway.util.ResponseUtil;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RoleFilter extends AbstractGatewayFilterFactory<RoleFilter.Config> {

  public RoleFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-Authenticated-Roles");

      if (rolesHeader == null) {
        return ResponseUtil.writeError(
            exchange,
            HttpStatus.FORBIDDEN,
            "Forbidden",
            "Missing roles in request"
        );
      }

      List<String> userRoles = List.of(rolesHeader.split(","));
      boolean hasAccess = config.requiredRoles.stream().anyMatch(userRoles::contains);

      if (!hasAccess) {
        return ResponseUtil.writeError(
            exchange,
            HttpStatus.FORBIDDEN,
            "Forbidden",
            "Access denied for current role"
        );
      }

      return chain.filter(exchange);
    };
  }

  @Data
  public static class Config {
    private List<String> requiredRoles;
  }
}
