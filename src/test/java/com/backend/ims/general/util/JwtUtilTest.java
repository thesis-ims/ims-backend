package com.backend.ims.general.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

@Test
public class JwtUtilTest {
  private static final String USERNAME = "testUser";
  private static final List<String> ROLES = List.of("ROLE_USER");
  private static final String SECRET_KEY = "dGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmc=";

  private JwtUtil jwtUtil;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    jwtUtil = new JwtUtil();
  }

  private String generateExpiredToken(String username) {
    return Jwts.builder()
      .setSubject(username)
      .claim("roles", ROLES)
      .setExpiration(new Date(System.currentTimeMillis() - 1000))
      .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
      .compact();
  }

  @Test
  public void testGenerateToken() {
    try {
      String token = jwtUtil.generateToken(USERNAME, ROLES);
      Assert.assertNotNull(token);
    } catch (Exception e) {
      Assert.fail("Exception thrown while generating token");
    }
  }

  @Test
  public void testGenerateToken_WithNullUsername() {
    String token = jwtUtil.generateToken(null, ROLES);
    Assert.assertNotNull(token); // JWT allows null subject
  }

  @Test
  public void testExtractUsername() {
    try {
      String token = jwtUtil.generateToken(USERNAME, ROLES);
      String extractedUsername = jwtUtil.extractUsername(token);
      Assert.assertEquals(extractedUsername, USERNAME);
    } catch (Exception e) {
      Assert.fail("Exception thrown while extracting username");
    }
  }

  @Test
  public void testExtractRoles() {
    String token = jwtUtil.generateToken(USERNAME, ROLES);
    Claims claims = jwtUtil.extractClaims(token);
    List<String> extractedRoles = claims.get("roles", List.class);
    Assert.assertEquals(extractedRoles, ROLES);
  }

  @Test
  public void testTokenExpiration() {
    try {
      String token = jwtUtil.generateToken(USERNAME, ROLES);
      boolean isExpired = jwtUtil.isTokenExpired(token);
      Assert.assertFalse(isExpired);
    } catch (Exception e) {
      Assert.fail("Exception thrown while checking token expiration");
    }
  }

  @Test
  public void testValidateToken() {
    try {
      String token = jwtUtil.generateToken(USERNAME, ROLES);
      boolean isValid = jwtUtil.validateToken(token, USERNAME);
      Assert.assertTrue(isValid);
    } catch (Exception e) {
      Assert.fail("Exception thrown while validating token");
    }
  }

  @Test
  public void testExtractClaimsWithInvalidToken() {
    try {
      jwtUtil.extractClaims("invalidToken");
      Assert.fail("Expected RuntimeException was not thrown");
    } catch (RuntimeException e) {
      Assert.assertNotNull(e.getMessage());
    }
  }

  @Test
  public void testExtractClaimsWithExpiredToken() {
    String expiredToken = generateExpiredToken(USERNAME);
    Claims claims = jwtUtil.extractClaims(expiredToken);
    Assert.assertEquals(claims.getSubject(), USERNAME);
  }

  @Test
  public void testIsTokenExpiredWithInvalidToken() {
    try {
      boolean isExpired = jwtUtil.isTokenExpired("invalidToken");
      Assert.assertTrue(isExpired);
    } catch (Exception e) {
      Assert.fail("Exception thrown while checking invalid token expiration");
    }
  }

  @Test
  public void testValidateToken_InvalidUsername() {
    String token = jwtUtil.generateToken(USERNAME, ROLES);
    boolean isValid = jwtUtil.validateToken(token, "wrongUser");
    Assert.assertFalse(isValid);
  }
}
