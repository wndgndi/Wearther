package com.jh.wearther.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);  // 처리방식을 설정
    config.addAllowedOrigin("*");  // 모든 출처를 허용
    config.addAllowedHeader("*");  // 모든 헤더를 허용
    config.addAllowedMethod("*");  // 모든 방식을 허용

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}
