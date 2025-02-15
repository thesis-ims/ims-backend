package com.backend.ims.general.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JwtFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    final String authorizationHeader = request.getHeader("Authorization");

    String username = null;
    String token = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7);
      username = jwtUtil.extractUsername(token);
    }

    if (username != null && jwtUtil.validateToken(token, username)) {
      UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "", new ArrayList<>());
      // Set the authentication in the context
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>()));
    }
    chain.doFilter(request, response);
  }
}
