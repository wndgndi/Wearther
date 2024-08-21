package com.jh.wearther.domain.auth.service;

import com.jh.wearther.domain.member.constant.Role;
import com.jh.wearther.domain.member.dto.MemberRequest;
import com.jh.wearther.domain.member.dto.MemberResponse;
import com.jh.wearther.domain.member.entity.Member;
import com.jh.wearther.domain.member.repository.MemberRepository;
import com.jh.wearther.global.redis.RedisService;
import com.jh.wearther.global.security.jwt.TokenDto;
import com.jh.wearther.global.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;
  private final RedisService redisService;
  private  final PasswordEncoder passwordEncoder;

  @Value("${jwt.refresh-token-redis-duration}")
  private long duration;

  @Value("${jwt.refresh-token-prefix}")
  private String refreshTokenPrefix;

  public MemberResponse signUp(MemberRequest memberRequest) {
    memberRequest.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
    memberRequest.setRole(Role.ROLE_USER);
    Member member = memberRequest.toEntity();
    memberRepository.save(member);

    return MemberResponse.fromEntity(member);
  }


  public TokenDto login(MemberRequest memberRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = memberRequest.toAuthenticationToken();
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    TokenDto tokenDto = tokenProvider.generateToken(authentication);

    redisService.setDataExpire(refreshTokenPrefix + authentication.getName(), tokenDto.getRefreshToken(), duration);

    return tokenDto;
  }
}
