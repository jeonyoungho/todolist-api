package com.example.api.service;

import com.example.domain.member.CustomUserDetails;
import com.example.domain.member.MemberRepository;
import com.example.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.example.exception.ErrorCode.MEMBER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        return memberRepository.findByAccountId(accountId)
                .map(u -> new CustomUserDetails(u, Collections.singleton(new SimpleGrantedAuthority(u.getAuthority().getValue()))))
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
