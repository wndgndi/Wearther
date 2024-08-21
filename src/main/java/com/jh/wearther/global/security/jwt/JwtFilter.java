package com.jh.wearther.global.security.jwt;

import com.jh.wearther.global.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final String TOKEN_PREFIX = "Bearer ";
  private final String TOKEN_HEADER = "Authorization";

  private final TokenProvider tokenProvider;
  private final RedisService redisService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveToken(request);

    if(StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
      Authentication authentication = tokenProvider.getAuthentication(token);
      final String LOG_OUT_PREFIX = "LOGGED_OUT_" + authentication.getName();

      String isLogOut = redisService.getData(LOG_OUT_PREFIX + token);

      if(isLogOut != null) {
        throw new RuntimeException("이미 로그아웃된 회원입니다.");
      }

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.info(String.format("[%s] -> %s", this.tokenProvider.getMemberName(token),
          request.getRequestURI()));
    }
    filterChain.doFilter(request, response);  // 다음 필터로 요청을 전달
  }

  private String resolveToken(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if(StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
      return token.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}
