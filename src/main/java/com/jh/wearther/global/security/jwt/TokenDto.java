package com.jh.wearther.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {

  private String grantType;
  private String accessToken;
  private String refreshToken;
  private long accessTokenExpiresIn;

  @Getter
  @AllArgsConstructor
  public static class Request {

    private String accessToken;
    private String refreshToken;
  }
}
