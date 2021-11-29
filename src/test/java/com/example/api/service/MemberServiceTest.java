package com.example.api.service;

import com.example.api.dto.jwt.TokenDto;
import com.example.api.dto.member.MemberLoginRequestDto;
import com.example.api.dto.member.MemberSignUpRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.exception.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;
//    @Mock
//    AuthenticationManager authenticationManager;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void signUp_ValidInput_Success() {
        // given
        MemberSignUpRequestDto rq = MemberSignUpRequestDto.create(member.getAccountId(), "test-pw", "test-name", "test-city", "test-street", "test-zipcode");

        final long memberId = 1L;
        ReflectionTestUtils.setField(member, "id", memberId);

        // mocking
        when(memberRepository.save(any())).thenReturn(member);

        // when
        Long savedId = memberService.signUp(rq);

        // then
        assertThat(savedId).isNotNull();
        assertThat(savedId).isGreaterThanOrEqualTo(memberId);
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
        fail("회원 가입하려는 회원의 계정 ID는 중복이여선 안됩니다.");
    }

    @Test
    public void login_ValidInput_Success() {
        // given
        MemberLoginRequestDto rq = MemberLoginRequestDto.create(member.getAccountId(), member.getAccountPw());

        // mocking
//        OngoingStubbing<AuthenticationManager> manager = when(authenticationManagerBuilder.getObject())
//                .thenReturn(new AuthenticationManager() {
//                    @Override
//                    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
//                        UserDetails principal = new User("test-id", "", authorities);
//                        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
//                    }
//                });
//        when(authenticationManagerBuilder.getObject().authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(null, null));

        // when
        TokenDto token = memberService.login(rq);
        System.out.println("token = " + token);


        // then
    }


}