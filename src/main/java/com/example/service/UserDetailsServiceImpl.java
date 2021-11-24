package com.example.service;

import com.example.domain.member.CustomUserDetails;
import com.example.domain.member.MemberRepository;
import com.example.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        return memberRepository.findByAccountId(accountId)
                .map(u -> new CustomUserDetails(u, Collections.singleton(new SimpleGrantedAuthority(u.getAuthority().getValue()))))
                .orElseThrow(() -> new MemberNotFoundException("Could not found member with account id: " + accountId));
    }
}
