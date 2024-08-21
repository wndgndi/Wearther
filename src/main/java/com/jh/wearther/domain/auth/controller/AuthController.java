package com.jh.wearther.domain.auth.controller;

import com.jh.wearther.domain.auth.service.AuthService;
import com.jh.wearther.domain.member.dto.MemberRequest;
import com.jh.wearther.domain.member.dto.MemberResponse;
import com.jh.wearther.global.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/signup")
  public ResponseEntity<MemberResponse> signUp(@RequestBody MemberRequest memberRequest) {
    return ResponseEntity.ok(authService.signUp(memberRequest));
  }

  @PostMapping("/auth/login")
  public ResponseEntity<TokenDto> login(@RequestBody MemberRequest memberRequest) {
    return ResponseEntity.ok(authService.login(memberRequest));
  }

}
