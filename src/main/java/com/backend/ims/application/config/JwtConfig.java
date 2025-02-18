package com.backend.ims.application.config;

import com.backend.ims.general.util.JwtFilter;
import com.backend.ims.general.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter();
  }

  @Bean
  public JwtUtil jwtUtil() {
    return new JwtUtil();
  }
}
