package com.jh.wearther.domain.member.entity;

import com.jh.wearther.domain.auth.constant.LoginType;
import com.jh.wearther.domain.member.constant.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String memberName;

  private String password;

  private String name;

  private String nickName;

  private String email;

  private String phone;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String profileImage; // 프로필 이미지

  private LoginType loginType;  // KAKAO, NAVER, GOOGLE

  private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)
}
