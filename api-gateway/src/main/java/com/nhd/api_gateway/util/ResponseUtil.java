package com.nhd.api_gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ResponseUtil {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static Mono<Void> writeError(ServerWebExchange exchange,
      HttpStatus status,
      String message,
      String data) {
    try {
      exchange.getResponse().setStatusCode(status);
      exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

      Map<String, Object> body = Map.of(
          "timestamp", LocalDateTime.now().toString(),
          "statusCode", status.value(),
          "message", message,
          "data", data
      );

      byte[] bytes = mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
      DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

      return exchange.getResponse().writeWith(Mono.just(buffer));
    } catch (Exception e) {
      return exchange.getResponse().setComplete();
    }
  }
}
