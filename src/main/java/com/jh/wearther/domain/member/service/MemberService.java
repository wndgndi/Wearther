package com.jh.wearther.domain.member.service;

import com.jh.wearther.domain.member.dto.MemberResponse;
import com.jh.wearther.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

}
