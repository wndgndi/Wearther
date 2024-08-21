package com.jh.wearther.domain.member.dto;

import com.jh.wearther.domain.auth.constant.LoginType;
import com.jh.wearther.domain.member.constant.Role;
import com.jh.wearther.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
@ToString
public class MemberRequest {
  private String memberName;

  private String password;

  private String name;

  private String nickName;

  private String email;

  private String phone;

  private Role role;

  private String profileImage;

  private LoginType loginType;

  public Member toEntity() {
    return Member.builder()
        .memberName(memberName)
        .password(password)
        .name(name)
        .nickName(nickName)
        .email(email)
        .phone(phone)
        .role(role)
        .profileImage(profileImage)
        .loginType(loginType)
        .build();
  }

  public UsernamePasswordAuthenticationToken toAuthenticationToken() {
    return new UsernamePasswordAuthenticationToken(memberName, password);
  }
}
