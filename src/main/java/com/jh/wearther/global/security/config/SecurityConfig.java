package com.jh.wearther.global.security.config;

import com.jh.wearther.global.security.jwt.JwtAccessDeniedHandler;
import com.jh.wearther.global.security.jwt.JwtAuthenticationEntryPoint;
import com.jh.wearther.global.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtFilter jwtFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.httpBasic(HttpBasicConfigurer::disable)
        .csrf(CsrfConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(
            matcher -> matcher.requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
        )
        .exceptionHandling(
            exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 인증이 실패했을 때 작동
                .accessDeniedHandler(jwtAccessDeniedHandler)  // 사용자가 인증은 되었지만, 요청한 리소스에 대한 접근 권한이 없을 때 작동
            )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
    ;

    return http.build();
  }

}
