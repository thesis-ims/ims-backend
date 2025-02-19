package com.backend.ims.general.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class JwtFilterTest {
  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN = "token";
  public static final String USERNAME = "test";
  public static final String JWT_UTIL = "jwtUtil";
  public static final String BEARER = "Bearer ";
  public static final String INVALID_PREFIX_TOKEN = "InvalidPrefix token";

  private JwtFilter jwtFilter;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain chain;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtFilter = new JwtFilter();
    ReflectionTestUtils.setField(jwtFilter, JWT_UTIL, jwtUtil);
    SecurityContextHolder.clearContext();
  }

  @Test
  public void testNoAuthorizationHeader() throws Exception {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(null);
    jwtFilter.doFilter(request, response, chain);

    Mockito.verify(chain).doFilter(request, response);
    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testInvalidAuthorizationHeaderPrefix() throws Exception {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(INVALID_PREFIX_TOKEN);
    jwtFilter.doFilter(request, response, chain);

    Mockito.verify(chain).doFilter(request, response);
    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    Mockito.verifyNoInteractions(jwtUtil);
  }

  @Test
  public void testValidToken() throws Exception {
    String token = TOKEN;
    String username = USERNAME;
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + token);
    Mockito.when(jwtUtil.extractUsername(token)).thenReturn(username);
    Mockito.when(jwtUtil.validateToken(token, username)).thenReturn(true);
    jwtFilter.doFilter(request, response, chain);

    Mockito.verify(chain).doFilter(request, response);
    Mockito.verify(jwtUtil).extractUsername(token);
    Mockito.verify(jwtUtil).validateToken(token, username);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Assert.assertNotNull(authentication);
    Assert.assertEquals(authentication.getPrincipal().getClass(), User.class);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Assert.assertEquals(userDetails.getUsername(), username);
    Assert.assertTrue(userDetails.getAuthorities().isEmpty());
  }

  @Test(groups = "small")
  public void testInvalidToken() throws Exception {
    String token = TOKEN;
    String username = USERNAME;
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + token);
    Mockito.when(jwtUtil.extractUsername(token)).thenReturn(username);
    Mockito.when(jwtUtil.validateToken(token, username)).thenReturn(false);
    jwtFilter.doFilter(request, response, chain);

    Mockito.verify(chain).doFilter(request, response);
    Mockito.verify(jwtUtil).extractUsername(token);
    Mockito.verify(jwtUtil).validateToken(token, username);
    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testExtractUsernameReturnsNull() throws Exception {
    String token = TOKEN;
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + token);
    Mockito.when(jwtUtil.extractUsername(token)).thenReturn(null);
    jwtFilter.doFilter(request, response, chain);

    Mockito.verify(chain).doFilter(request, response);
    Mockito.verify(jwtUtil).extractUsername(token);
    Mockito.verify(jwtUtil, Mockito.never()).validateToken(Mockito.any(), Mockito.any());
    Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
