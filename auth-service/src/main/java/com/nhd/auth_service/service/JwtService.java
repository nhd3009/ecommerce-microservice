package com.nhd.auth_service.service;

import com.nhd.auth_service.entity.Role;
import com.nhd.auth_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long jwtExpirationMs;

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.getUsername());
    claims.put("email", user.getEmail());
    claims.put("roles", user.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.toList()));

    return Jwts.builder()
        .setHeaderParam("typ", "JWT")
        .setClaims(claims)
        .setSubject(String.valueOf(user.getId()))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Long extractUserId(String token) {
    return Long.valueOf(extractAllClaims(token).getSubject());
  }

  public String extractUsername(String token) {
      return extractAllClaims(token).get("username", String.class);
  }

  public List<String> extractRoles(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("roles", List.class);
  }

  public boolean isTokenValid(String token, User user) {
    final String username = extractUsername(token);
    return (username.equals(user.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
