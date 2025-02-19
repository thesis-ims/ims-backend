package com.backend.ims.general.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

@Test
public class JwtUtilTest {
  public static final String USERNAME = "testUser";
  private JwtUtil jwtUtil;
  private static final String SECRET_KEY = "dGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmcgdGhpcyBzdHJpbmc=";

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    jwtUtil = new JwtUtil();
  }

  private String generateExpiredToken(String username) {
    return Jwts.builder()
      .setSubject(username)
      .setExpiration(new Date(System.currentTimeMillis() - 1000))
      .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
      .compact();
  }

  @Test
  public void testGenerateToken() {
    try {
      String token = jwtUtil.generateToken(USERNAME);
      Assert.assertNotNull(token);
    } catch (Exception e) {
      Assert.fail("Exception thrown while generating token");
    }
  }

  @Test
  public void testGenerateToken_Failure() {
    try {
      jwtUtil.generateToken(null);
    } catch (Exception e) {
      Assert.fail("Exception thrown while generating token");
    }
  }

  @Test
  public void testExtractUsername() {
    try {
      String token = jwtUtil.generateToken(USERNAME);
      String extractedUsername = jwtUtil.extractUsername(token);
      Assert.assertEquals(extractedUsername, USERNAME);
    } catch (Exception e) {
      Assert.fail("Exception thrown while extracting username");
    }
  }

  @Test
  public void testTokenExpiration() {
    try {
      String token = jwtUtil.generateToken(USERNAME);
      boolean isExpired = jwtUtil.isTokenExpired(token);
      Assert.assertFalse(isExpired);
    } catch (Exception e) {
      Assert.fail("Exception thrown while checking token expiration");
    }
  }

  @Test
  public void testValidateToken() {
    try {
      String token = jwtUtil.generateToken(USERNAME);
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

    try {
      jwtUtil.extractClaims(expiredToken);
    } catch (ExpiredJwtException e) {
      Assert.assertNotNull(e.getClaims());
    }
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
}
