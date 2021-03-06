package com.example.api.service;

import com.example.api.dto.jwt.ReissueRequestDto;
import com.example.api.dto.jwt.TokenDto;
import com.example.api.dto.member.MemberListResponseDto;
import com.example.api.dto.member.MemberSignUpRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.exception.CustomException;
import com.example.jwt.TokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void signUp_ValidInput_Success() {
        // given
        final long memberId = 1L;
        ReflectionTestUtils.setField(member, "id", memberId);

        MemberSignUpRequestDto rq = MemberSignUpRequestDto.create(member.getAccountId(), "test-pw", "test-name", "test-city", "test-street", "test-zipcode");

        // mocking
        when(memberRepository.save(any())).thenReturn(member);

        // when
        Long savedId = memberService.signUp(rq);

        // then
        assertAll(
                () -> assertThat(savedId).isNotNull(),
                () -> assertThat(savedId).isGreaterThanOrEqualTo(memberId),
                () -> verify(memberRepository).save(any()),
                () -> verify(memberRepository, times(1)).save(any())
        );
    }

    @Test(expected = CustomException.class)
    public void signUp_DuplicatedAccountId_ThrowCustomException() {
        // given
        MemberSignUpRequestDto rq = MemberSignUpRequestDto.create(member.getAccountId(), "test-pw", "test-name", "test-city", "test-street", "test-zipcode");

        // mocking
        when(memberRepository.existsByAccountId(anyString())).thenReturn(true);

        // when
        Long savedId = memberService.signUp(rq);

        // then
        fail("????????? ?????? ID ?????? ????????? ????????? ???????????? ?????????.");
    }

   @Test
   public void logout_ValidInput_Success() {
       // given
       final String accountId = "test-id";

       // mocking
       when(refreshTokenService.hasKey(anyString())).thenReturn(true);
       doNothing().when(refreshTokenService).delValue(anyString());

       // when
       memberService.logout(accountId);

       // then
       assertAll(
               () -> verify(refreshTokenService).hasKey(anyString()),
               () -> verify(refreshTokenService, times(1)).hasKey(anyString()),

               () -> verify(refreshTokenService).delValue(anyString()),
               () -> verify(refreshTokenService, times(1)).delValue(anyString())
       );

   }

    @Test(expected = CustomException.class)
    public void logout_NotExistedKeyInRefreshTokenStore_ThrowCustomException() {
        // given
        final String accountId = "fake-account-id";

        // mocking
        when(refreshTokenService.hasKey(anyString())).thenReturn(false);

        // when
        memberService.logout(accountId);

        // then
        fail("???????????? ?????? ?????????????????? ?????? ????????? ????????? ????????? ???????????? ?????????.");
    }

   @Test
   public void reissue_ValidInput_Success() {
       // given
       final String accessToken = "access-token";
       final String refreshToken = "refresh-token";
       ReissueRequestDto rq = ReissueRequestDto.create(accessToken, refreshToken);
       TokenDto tokenDto = TokenDto.builder().build();

       Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
       UserDetails principal = new User(member.getAccountId(), "", authorities);
       Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

       // mocking
       when(tokenProvider.validateToken(rq.getRefreshToken())).thenReturn(true);
       when(tokenProvider.getAuthentication(rq.getAccessToken())).thenReturn(authentication);
       when(refreshTokenService.getValue(authentication.getName())).thenReturn(refreshToken);
       when(tokenProvider.generateTokenDto(authentication)).thenReturn(tokenDto);
       doNothing().when(refreshTokenService).setValue(authentication.getName(), tokenDto.getRefreshToken());

       // when
       memberService.reissue(rq);

       // then
       assertAll(
               () -> verify(tokenProvider).validateToken(rq.getRefreshToken()),
               () -> verify(tokenProvider, times(1)).validateToken(rq.getRefreshToken()),

               () -> verify(tokenProvider).getAuthentication(rq.getAccessToken()),
               () -> verify(tokenProvider, times(1)).getAuthentication(rq.getAccessToken()),

               () -> verify(refreshTokenService).getValue(authentication.getName()),
               () -> verify(refreshTokenService, times(1)).getValue(authentication.getName()),

               () -> verify(tokenProvider).generateTokenDto(authentication),
               () -> verify(tokenProvider, times(1)).generateTokenDto(authentication),

               () -> verify(refreshTokenService).setValue(authentication.getName(), tokenDto.getRefreshToken()),
               () -> verify(refreshTokenService, times(1)).setValue(authentication.getName(), tokenDto.getRefreshToken())
       );
   }

    @Test(expected = CustomException.class)
    public void reissue_InValidToken_ThrowCustomException() {
        // given
        ReissueRequestDto rq = ReissueRequestDto.create("access-token", "refresh-token");

        // mocking
        when(tokenProvider.validateToken(rq.getRefreshToken())).thenReturn(false);

        // when
        memberService.reissue(rq);

        // then
        fail("????????? ????????? ????????? ????????? ???????????? ?????????.");
    }

    @Test(expected = CustomException.class)
    public void reissue_SavedRefreshTokenIsNUll_ThrowCustomException() {
        // given
        ReissueRequestDto rq = ReissueRequestDto.create("access-token", "refresh-token");

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        // mocking
        when(tokenProvider.validateToken(rq.getRefreshToken())).thenReturn(true);
        when(tokenProvider.getAuthentication(rq.getAccessToken())).thenReturn(authentication);
        when(refreshTokenService.getValue(authentication.getName())).thenReturn(null);

        // when
        memberService.reissue(rq);

        // then
        fail("Token ?????????????????? ????????? RefreshToken ?????? Null ????????? ????????? ????????? ???????????? ?????????.");
    }

    @Test(expected = CustomException.class)
    public void reissue_SavedRefreshTokenIsEmpty_ThrowCustomException() {
        // given
        ReissueRequestDto rq = ReissueRequestDto.create("access-token", "refresh-token");

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        // mocking
        when(tokenProvider.validateToken(rq.getRefreshToken())).thenReturn(true);
        when(tokenProvider.getAuthentication(rq.getAccessToken())).thenReturn(authentication);
        when(refreshTokenService.getValue(authentication.getName())).thenReturn("");

        // when
        memberService.reissue(rq);

        // then
        fail("Token ?????????????????? ????????? RefreshToken ?????? ??? ????????? ????????? ????????? ???????????? ?????????.");
    }

    @Test(expected = CustomException.class)
    public void reissue_ReceivedRefreshTokenAndSavedRefreshTokenIsDifferent_ThrowCustomException() {
        // given
        final String accessToken = "access-token";
        final String refreshToken = "refresh-token";
        ReissueRequestDto rq = ReissueRequestDto.create(accessToken, refreshToken);

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        // mocking
        when(tokenProvider.validateToken(rq.getRefreshToken())).thenReturn(true);
        when(tokenProvider.getAuthentication(rq.getAccessToken())).thenReturn(authentication);
        when(refreshTokenService.getValue(authentication.getName())).thenReturn(refreshToken + "2");

        // when
        memberService.reissue(rq);

        // then
        fail("?????? ?????? RefreshToken ?????? Token ?????????????????? ????????? RefreshToken ?????? ??????????????? ????????? ????????? ???????????? ?????????.");
    }

    @Test
    public void findAll_ValidInput_Success() {
        // given
        List<Member> memberList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            memberList.add(Member.create("test-id" + i, "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER));
        }

        // mocking
        when(memberRepository.findAll()).thenReturn(memberList);

        // when
        MemberListResponseDto result = memberService.findAll();
        List<Member> resultList = result.getMembers();

        // then
        assertAll(
                () -> assertThat(resultList).isNotNull(),
                () -> assertThat(resultList).isEqualTo(memberList),

                () -> verify(memberRepository).findAll(),
                () -> verify(memberRepository, times(1)).findAll()
        );
    }

}
