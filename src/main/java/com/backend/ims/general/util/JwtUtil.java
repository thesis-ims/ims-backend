package com.backend.ims.general.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
  private static final String SECRET_KEY = "dGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmc=";
  private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 3; // 3 hours

  public String generateToken(String username) {
    try {
      return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
        .compact();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error generating token", e);
    }
  }

  public Claims extractClaims(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(SECRET_KEY.getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error extracting claims", e);
    }
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
  }

  public boolean validateToken(String token, String username) {
    return (username.equals(extractUsername(token)) && !isTokenExpired(token));
  }
}
