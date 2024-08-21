package com.jh.wearther.global.security.jwt;

import com.jh.wearther.global.exception.CustomException;
import com.jh.wearther.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenProvider {

  private final String KEY_ROLES = "role";
  private final String BEARER_TYPE = "bearer";
  private final long ACCESS_TOKEN_EXPIRES_IN_SECONDS;
  private final long REFRESH_TOKEN_EXPIRES_IN_SECONDS;

  private Key key;

  public TokenProvider(@Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-token-expires-in-seconds}") long accessTokenExpiresInSeconds,
      @Value("${jwt.refresh-token-expires-in-seconds}") long refreshTokenExpiresInSeconds){
    byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64로 인코딩된 문자열을 원래의 바이트 배열로 복원
    this.key = Keys.hmacShaKeyFor(keyBytes); // // JWT 를 서명할 때 사용되고, 서명된 토큰을 검증할 때 필요한  HMAC SHA 알고리즘을 적용한 key 를 생성
    this.ACCESS_TOKEN_EXPIRES_IN_SECONDS = accessTokenExpiresInSeconds;
    this.REFRESH_TOKEN_EXPIRES_IN_SECONDS = refreshTokenExpiresInSeconds;
  }

  public TokenDto generateToken(Authentication authentication) {

    // 인증된 사용자의 권한을 가져와서, 이를 쉼표로 구분된 문자열로 변환하여 authorities 변수에 저장
    // 이 문자열은 JWT 토큰의 클레임(claim)으로 포함되어, 나중에 토큰을 검증하거나 사용자 권한을 확인할 때 사용
    String authorities = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = new Date().getTime();
    Date accessTokenExpiresAt = new Date(now + ACCESS_TOKEN_EXPIRES_IN_SECONDS);

    String accessToken = Jwts.builder()
        .setSubject(authentication.getName())  // 현재 인증된 사용자의 이름(또는 ID)을 주체로 설정, 주체는 JWT의 소유자를 나타냄
        .claim(KEY_ROLES, authorities)  // 사용자의 권한 정보를 추가하는 부분으로, KEY_ROLES는 이 정보를 식별하는 키이며, authorities는 실제 권한 값
        .setIssuedAt(new Date())  // JWT가 발급된 시간
        .setExpiration(accessTokenExpiresAt)  // JWT의 만료 시간
        .signWith(key, SignatureAlgorithm.HS512)  // JWT를 서명하는 방법
        .compact();

    String refreshToken = Jwts.builder()
        .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRES_IN_SECONDS))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();

    return TokenDto.builder()
        .grantType(BEARER_TYPE)
        .accessToken(accessToken)
        .accessTokenExpiresIn(accessTokenExpiresAt.getTime())
        .refreshToken(refreshToken)
        .build();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token);  // 주어진 비밀 키로 JWT 토큰의 서명을 검증하고 토큰의 본문(클레임)을 파싱하여 반환
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }

    return false;
  }

  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);  // JWT 토큰을 파싱하여 클레임(Claims)을 추출(클레임은 JWT의 본문에 포함된 정보로, 사용자의 정보나 권한 등을 담고 있음)

    if(claims.get(KEY_ROLES) != null){  //  만약 KEY_ROLES가 null이라면, 사용자가 권한이 없다는 것을 의미하므로 CustomException을 발생
      throw new CustomException(ErrorCode.NO_AUTHORITIES);
    }


    Collection<? extends GrantedAuthority> roles =
        Arrays.stream(claims.get(KEY_ROLES).toString().split(","))  // // KEY_ROLES에서 가져온 권한 문자열을 쉼표로 분리하여 배열로 만듦 (예를 들어, "ROLE_USER,ROLE_ADMIN"과 같은 문자열이 있을 경우, 이를 ["ROLE_USER", "ROLE_ADMIN"]로 변환)
            .map(SimpleGrantedAuthority::new)  // 각 권한 문자열을 SimpleGrantedAuthority 객체로 변환
            .toList();  // 최종적으로 변환된 권한 객체들을 리스트로 만듦

    // User 객체를 생성. 이 객체는 Spring Security에서 사용자의 정보를 나타내며, 사용자 이름, 비밀번호(여기서는 빈 문자열), 권한 목록을 포함
    UserDetails principal = new User(claims.getSubject(), "", roles);

    // Spring Security에서 제공하는 Authentication 인터페이스의 구현체로, 사용자 이름, 비밀번호(여기서는 빈 문자열), 권한 목록을 포함
    // 이 객체는 인증된 사용자의 정보를 나타내며, Spring Security의 컨텍스트에 저장되어 사용
    return new UsernamePasswordAuthenticationToken(principal, "", roles);
  }

  public Claims parseClaims(String token) {
    try{
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public String getMemberName(String token) {
    return parseClaims(token).getSubject();
  }

}
