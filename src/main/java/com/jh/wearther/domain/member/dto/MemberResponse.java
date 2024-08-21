package com.jh.wearther.domain.member.dto;

import com.jh.wearther.domain.auth.constant.LoginType;
import com.jh.wearther.domain.member.constant.Role;
import com.jh.wearther.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
  private String memberName;

  private String name;

  private String nickName;

  private String email;

  private String phone;

  private Role role;

  private String profileImage;

  private LoginType loginType;

  public static MemberResponse fromEntity(Member member) {
    return MemberResponse.builder()
        .memberName(member.getMemberName())
        .name(member.getName())
        .nickName(member.getNickName())
        .email(member.getEmail())
        .phone(member.getPhone())
        .role(member.getRole())
        .profileImage(member.getProfileImage())
        .loginType(member.getLoginType())
        .build();
  }
}
