package com.leduy8.springbootjava.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public boolean validateToken(String token, String email) {
    return email.equals(extractEmail(token)) && !isTokenExpired(token);
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    return claimsResolver.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }
}
