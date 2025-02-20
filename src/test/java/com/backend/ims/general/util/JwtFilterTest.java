package com.backend.ims.general.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Test
public class JwtFilterTest {
  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN = "token";
  public static final String USERNAME = "test";
  public static final String JWT_UTIL = "jwtUtil";
  public static final String BEARER = "Bearer ";

  private JwtFilter jwtFilter;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain chain;
  @Mock
  private Claims mockClaims;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtFilter = new JwtFilter();
    ReflectionTestUtils.setField(jwtFilter, JWT_UTIL, jwtUtil);
    SecurityContextHolder.clearContext();
  }

  @Test
  public void testValidToken() throws Exception {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + TOKEN);
    Mockito.when(jwtUtil.extractUsername(TOKEN)).thenReturn(USERNAME);
    Mockito.when(jwtUtil.validateToken(TOKEN, USERNAME)).thenReturn(true);

    Mockito.when(jwtUtil.extractClaims(TOKEN)).thenReturn(mockClaims);
    Mockito.when(mockClaims.get("roles", List.class)).thenReturn(List.of("ROLE_USER"));

    jwtFilter.doFilter(request, response, chain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Assert.assertEquals(
      authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList()),
      List.of("ROLE_USER")
    );
  }

  @Test(groups = "small")
  public void testInvalidToken() throws Exception {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + TOKEN);
    Mockito.when(jwtUtil.extractUsername(TOKEN)).thenReturn(USERNAME);
    Mockito.when(jwtUtil.validateToken(TOKEN, USERNAME)).thenReturn(false);

    Mockito.when(jwtUtil.extractClaims(TOKEN)).thenReturn(mockClaims);
    Mockito.when(mockClaims.get("roles", List.class)).thenReturn(Collections.emptyList());

    jwtFilter.doFilter(request, response, chain);

    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testExtractUsernameReturnsNull() throws Exception {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + TOKEN);
    Mockito.when(jwtUtil.extractUsername(TOKEN)).thenReturn(null);

    Mockito.when(jwtUtil.extractClaims(TOKEN)).thenReturn(mockClaims);
    Mockito.when(mockClaims.get("roles", List.class)).thenReturn(Collections.emptyList());

    jwtFilter.doFilter(request, response, chain);

    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
