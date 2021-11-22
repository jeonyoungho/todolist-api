package com.example.config.auth;

import com.example.domain.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticationFilter 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        final String accountId = token.getName();
        final String accountPw = (String) token.getCredentials();

        // UserDetailsService 를 통해 DB에서 계정 아이디로 사용자 조회
        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(accountId);

        if (!passwordEncoder.matches(accountPw, userDetails.getPassword())) {
            throw new BadCredentialsException(userDetails.getAccountId() + " received an invalid password.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, accountPw, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
