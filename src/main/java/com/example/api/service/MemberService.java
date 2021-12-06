package com.example.api.service;

import com.example.api.dto.jwt.ReissueRequestDto;
import com.example.api.dto.jwt.TokenDto;
import com.example.api.dto.member.MemberListResponseDto;
import com.example.api.dto.member.MemberLoginRequestDto;
import com.example.api.dto.member.MemberSignUpRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.exception.CustomException;
import com.example.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.example.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public Long signUp(final MemberSignUpRequestDto rq) {
        String accountId = rq.getAccountId();
        if (memberRepository.existsByAccountId(accountId)) {
            throw new CustomException(DUPLICATE_RESOURCE);
        }

        Member savedMember = memberRepository.save(rq.toEntity(passwordEncoder));
        return savedMember.getId();
    }

    @Transactional
    public TokenDto login(MemberLoginRequestDto rq) {
        // 1. accountId / accountPw 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = rq.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크)가 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsServices에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        refreshTokenService.setValue(rq.getAccountId(), tokenDto.getRefreshToken());

        return tokenDto;
    }

    @Transactional
    public void logout(String accountId) {
        if (!refreshTokenService.hasKey(accountId)) {
            throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
        }

        refreshTokenService.delValue(accountId);
    }

    @Transactional
    public TokenDto reissue(ReissueRequestDto rq) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(rq.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 Member Id 가져오기
        Authentication authentication = tokenProvider.getAuthentication(rq.getAccessToken());

        // 3. Refresh Token 저장소에서 accountId를 기반으로 Refresh Token 값 가져옴
        String accountId = authentication.getName();
        String receivedRefreshToken = rq.getRefreshToken();
        String savedRefreshToken = refreshTokenService.getValue(accountId);

        // 4. RefreshToken 일치하는지 검사
        checkValidRefreshToken(receivedRefreshToken, savedRefreshToken);

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        refreshTokenService.setValue(accountId, tokenDto.getRefreshToken());

        return tokenDto;
    }

    private void checkValidRefreshToken(String receivedRefreshToken, String savedRefreshToken) {
        if (!StringUtils.hasText(savedRefreshToken) ) {
            throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
        }

        if (!receivedRefreshToken.equals(savedRefreshToken)) {
            throw new CustomException(MISMATCH_REFRESH_TOKEN);
        }
    }

    public MemberListResponseDto findAll() {
        return MemberListResponseDto.builder()
                .members(memberRepository.findAll())
                .build();
    }
}
